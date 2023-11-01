package dev.fastcampus.payment.service.api

import dev.fastcampus.payment.service.CaptureMarker
import dev.fastcampus.payment.service.OrderService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class PaymentApi(
    @Value("\${payment.self.domain}")
    domain: String,
    private val captureMarker: CaptureMarker,
) {
    private val client = WebClient.builder().baseUrl(domain)
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()

    suspend fun recapture(orderId: Long) {
        captureMarker.put(orderId)
        client.put().uri("/order/recapture/$orderId").retrieve()
            .bodyToMono<String>().subscribe()
    }
}