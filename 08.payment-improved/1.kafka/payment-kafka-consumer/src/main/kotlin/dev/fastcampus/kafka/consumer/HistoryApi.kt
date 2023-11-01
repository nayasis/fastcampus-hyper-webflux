package dev.fastcampus.kafka.consumer

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.LocalDateTime

@Service
class HistoryApi(
    @Value("\${api.history.domain}")
    private val domain: String
) {

    private val client = WebClient.builder().baseUrl(domain)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun save(order: Order) {
        client.post().uri("/history").bodyValue(order.toReqSaveHistory()).awaitExchange {}
    }

}

data class ReqSaveHistory(
    var orderId: Long,
    var userId: Long?,
    var description: String?,
    var amount: Long?,
    var status: Status?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
)

enum class Status {
    CREATE,
    AUTH_SUCCESS,
    AUTH_FAIL,
    AUTH_INVALID,
    CAPTURE_REQUEST,
    CAPTURE_RETRY,
    CAPTURE_SUCCESS,
    CAPTURE_FAIL,
}

data class Order(
    var id: Long,
    var userId: Long,
    var description: String?,
    var amount: Long,
    var pgOrderId: String,
    var pgKey: String,
    var pgStatus: Status,
    var pgRetryCount: Int,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    fun toReqSaveHistory(): ReqSaveHistory { return ReqSaveHistory(
        orderId =  id,
        userId = userId,
        description = description,
        amount = amount,
        status = pgStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )}
}