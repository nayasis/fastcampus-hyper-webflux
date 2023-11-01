package dev.fastcampus.elasticsearch.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "history")
data class History(
    @Id
    var orderId: Long = 0,
    var userId: Long = 0,
    @Field(type = FieldType.Text)
    var description: String = "",
    var amount: Long = 0,
    var status: Status = Status.CREATE,
    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    var updatedAt: LocalDateTime = LocalDateTime.now(),
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
