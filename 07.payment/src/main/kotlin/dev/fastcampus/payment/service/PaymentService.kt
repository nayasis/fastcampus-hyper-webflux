package dev.fastcampus.payment.service

import dev.fastcampus.payment.controller.PaymentSuccess
import dev.fastcampus.payment.exception.InvalidPaymentException
import dev.fastcampus.payment.exception.NotFoundException
import dev.fastcampus.payment.model.enum.TxStatus
import dev.fastcampus.payment.repository.OrderRepository
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

private val SECRET_KEY = "dGVzdF9za19PRVA1OUx5Ylo4QldrMlpMRzFaMzZHWW83cFJlOg=="

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderRepository: OrderRepository,
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

        logger.debug { ">> request : ${request}" }

        var order = orderRepository.findByPaymentOrderId(request.orderId)?.also {
            if(request.amount != it.amount)
                throw InvalidPaymentException("Invalid order amount")
        } ?: throw InvalidPaymentException("No order found")

        logger.debug { ">> order : ${order}" }

//        request.orderId = "hacked-id-1234"

        try {
            val res = client.post().uri("/v1/payments/confirm")
                .header("Authorization", "Basic $SECRET_KEY")
                .bodyValue(request)
                .retrieve()
                .awaitBody<ConfirmMessage>()

            logger.debug { ">> confirm res\n$res" }

            order.txid = res.paymentKey
            if(res.totalAmount == order.amount) {
                order.status = TxStatus.SUCCESS
            } else {
                order.status = TxStatus.NEED_CHECK
            }
            return true

        } catch (e: Exception) {
            logger.error(e.message, e)
            order.status = TxStatus.FAIL
            return false
        } finally {
            // reactive code 임. 꼭 저장을 해줄 것 !
            orderRepository.save(order)
        }

    }

}

data class ConfirmMessage(
    val paymentKey: String,
    val orderId: String,
    val totalAmount: Long,
    val method: String,
)