package dev.fastcampus.payment.service

import dev.fastcampus.payment.controller.PaymentSuccess
import dev.fastcampus.payment.repository.OrderRepository
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
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


    suspend fun confirm(request: PaymentSuccess) {
        val res = client.post().uri("/v1/payments/confirm")
            .header("Authorization", "Basic $SECRET_KEY")
            .bodyValue(request)
            .retrieve()
//            .bodyToMono(String::class.java)
            .bodyToMono(ConfirmMessage::class.java)
            .awaitSingle()

        logger.debug { ">> confirm res\n$res" }

    }



}

data class ConfirmMessage(
    val totalAmount: Long,
    val method: String,
)