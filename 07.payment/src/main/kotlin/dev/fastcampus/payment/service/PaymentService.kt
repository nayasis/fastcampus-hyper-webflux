package dev.fastcampus.payment.service

import dev.fastcampus.payment.controller.PaymentSuccess
import dev.fastcampus.payment.exception.InvalidPaymentException
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.repository.OrderRepository
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderService: OrderService,
    private val purchaseService: PurchaseService,
    @Value("\${payment.key.toss.secret}")
    private val secretKey: String,
) {

    private val client = createWebClient()

    private fun createWebClient(): WebClient {
        val insecureSslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        val provider = ConnectionProvider.builder("toss-payment")
            .maxConnections(100)
            .pendingAcquireTimeout(Duration.ofSeconds(240))
            .build()
        val connector = ReactorClientHttpConnector(HttpClient.create(provider).secure { it.sslContext(insecureSslContext) })
        return WebClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(connector)
            .build()
    }

    @Transactional
    suspend fun confirm(request: PaymentSuccess): Boolean {

        logger.debug { ">> request: $request" }

        val order = orderService.getByPaymentOrderId(request.orderId).also { order ->
                try {
                    if(order.amount != request.amount) {
                        order.status = TxStatus.INVALID_REQUEST
                        throw InvalidPaymentException("Invalid order amount (origin: ${order.amount}, payment: ${request.amount}")
                    } else {
                        order.status = TxStatus.REQUEST_CONFIRM
                    }
                } finally {
                    orderService.save(order)
                }
            }

        logger.debug { ">> order: $order" }

        return try {
            val res = client.post().uri("/v1/payments/confirm")
                .header("Authorization", "Basic $secretKey")
                .bodyValue(request)
                .retrieve()
                .awaitBody<ConfirmMessage>()

            logger.debug { ">> confirm res\n$res" }

            order.txid = res.paymentKey

            if(res.totalAmount == order.amount) {
                order.status = TxStatus.SUCCESS
                true
            } else {
                order.status = TxStatus.NEED_CHECK
                false
            }

        } catch (e: Exception) {
            logger.error(e.message, e)
            order.status = TxStatus.FAIL
            false
        } finally {
            order.let { orderService.save(it) }
            purchaseService.save(order)
        }

    }

}

data class ConfirmMessage(
    val paymentKey: String,
    val orderId: String,
    val totalAmount: Long,
    val method: String,
)