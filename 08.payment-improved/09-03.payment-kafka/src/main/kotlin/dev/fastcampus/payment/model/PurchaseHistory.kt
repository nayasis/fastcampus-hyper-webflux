package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import dev.fastcampus.payment.model.code.TxStatus
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType.Date
import java.time.LocalDateTime

@Document(indexName = "purchase-history")
class PurchaseHistory(
    @Id
    var orderId: Long = 0,
    var userId: Long = 0,
    var prodId: Long = 0,
    var prodNm: String = "",
    var description: String = "",
    var amount: Long = 0,
    var status: TxStatus = TxStatus.NONE,
    @Field(type = Date, format = [DateFormat.date_hour_minute_second_millis])
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Field(type = Date, format = [DateFormat.date_hour_minute_second_millis])
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    override fun equals(other:Any?): Boolean = kotlinEquals(other, arrayOf(PurchaseHistory::orderId))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(PurchaseHistory::orderId))
    override fun toString(): String = kotlinToString(arrayOf(
        PurchaseHistory::orderId,
        PurchaseHistory::userId,
        PurchaseHistory::prodId,
        PurchaseHistory::prodNm,
        PurchaseHistory::description,
        PurchaseHistory::amount,
        PurchaseHistory::status,
        PurchaseHistory::createdAt,
        PurchaseHistory::updatedAt,
    ))

}