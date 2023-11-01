package dev.fastcampus.payment.service

import com.fasterxml.jackson.databind.ObjectMapper
import dev.fastcampus.payment.common.Beans
import dev.fastcampus.payment.controller.ReqPayFailed
import dev.fastcampus.payment.controller.ReqPaySucceed
import dev.fastcampus.payment.controller.TossPaymentType
import dev.fastcampus.payment.exception.InvalidOrderStatus
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.PgStatus.*
import dev.fastcampus.payment.service.api.PaymentApi
import dev.fastcampus.payment.service.api.TossPayApi
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderService: OrderService,
    private val tossPayApi: TossPayApi,
    private val objectMapper: ObjectMapper,
    private val paymentApi: PaymentApi,
    private val captureMarker: CaptureMarker,
    private val kafkaPipeline: KafkaPipeline,
) {

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgKey = request.paymentKey
            pgStatus = AUTH_SUCCESS
        }
        try {
            return if(order.amount != request.amount) {
                logger.error { "Invalid auth because of amount (order: ${order.amount}, pay: ${request.amount})" }
                order.pgStatus = AUTH_INVALID
                false
            } else {
                true
            }
        } finally {
            orderService.save(order)
        }
    }

    @Transactional
    suspend fun authFailed(request: ReqPayFailed) {
        val order = orderService.getOrderByPgOrderId(request.orderId)
        if(order.pgStatus == CREATE) {
            order.pgStatus = AUTH_FAIL
            orderService.save(order)
        }
        logger.error { """
            >> Fail on error
              - request: $request
              - order  : $order
        """.trimIndent() }
    }

    @Transactional
    suspend fun capture(request: ReqPaySucceed) {
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgStatus = CAPTURE_REQUEST
            Beans.beanOrderService.save(this)
        }
        capture(order)
    }

    @Transactional
    suspend fun capture(order: Order) {
        logger.debug { ">> order: $order" }
        if(order.pgStatus !in setOf(CAPTURE_REQUEST, CAPTURE_RETRY))
            throw InvalidOrderStatus("invalid order status (orderId: ${order.id}, status: ${order.pgStatus}")
        order.increaseRetryCount()

        captureMarker.put(order.id)

        try {
            tossPayApi.confirm(order.toReqPaySucceed()).also { logger.debug { ">> res: $it" } }
            order.pgStatus = CAPTURE_SUCCESS
        } catch (e: Exception) {
            logger.error(e.message, e)
            order.pgStatus = when (e) {
                is WebClientRequestException -> CAPTURE_RETRY
                is WebClientResponseException -> {
                    val resError = e.toTossPayApiError()
                    logger.debug { ">> res error: $resError" }
                    when(resError.code) {
                        "ALREADY_PROCESSED_PAYMENT" -> CAPTURE_SUCCESS
                        "PROVIDER_ERROR", "FAILED_INTERNAL_SYSTEM_PROCESSING" -> CAPTURE_RETRY
                        else -> CAPTURE_FAIL
                    }
                }
                else -> CAPTURE_FAIL
            }
            if(order.pgStatus == CAPTURE_RETRY && order.pgRetryCount >= 3)
                order.pgStatus = CAPTURE_FAIL
            if(order.pgStatus != CAPTURE_SUCCESS)
                throw e
        } finally {
            orderService.save(order)
            kafkaPipeline.sendPayment(order)
            captureMarker.remove(order.id)
            if(order.pgStatus == CAPTURE_RETRY) {
                paymentApi.recapture(order.id)
            }
        }
    }

    suspend fun recaptureOnBoot() {
        val now = LocalDateTime.now()
        captureMarker.getAll()
            .filter { Duration.between(it.updatedAt!!,now).seconds >= 60 }
            .forEach { order ->
                captureMarker.remove(order.id)
                paymentApi.recapture(order.id)
            }
    }

    private fun Order.toReqPaySucceed(): ReqPaySucceed {
        return this.let {
            ReqPaySucceed(
                paymentKey = it.pgKey!!,
                orderId = it.pgOrderId!!,
                amount = it.amount,
                paymentType = TossPaymentType.NORMAL,
            )
        }
    }

    private fun WebClientResponseException.toTossPayApiError(): TossPayApiError {
        val json = String(this.responseBodyAsByteArray)
        return objectMapper.readValue(json, TossPayApiError::class.java)
    }

}

data class TossPayApiError(
    val code: String,
    val message: String,
)

