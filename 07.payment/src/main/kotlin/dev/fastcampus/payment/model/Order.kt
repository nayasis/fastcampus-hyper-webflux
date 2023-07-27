package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import dev.fastcampus.payment.common.Beans
import dev.fastcampus.payment.model.enum.TxStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID
import javax.annotation.processing.Generated

@Table("TB_ORDER")
class Order {

    @Id
    @Generated
    var id: Long = 0

    var userId: Long? = null

    var prodId: Long? = null

    var description: String? = null

    var amount: Long = 0

    var txid: String? = null

    var status: TxStatus = TxStatus.CREATE

    var paymentOrderId: String? = null

    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

    suspend fun getProduct(): Product? {
        return prodId?.let { Beans.productRepository.findById(it) }
    }

    constructor()
    constructor(userId: Long, prodId: Long, price: Long, description: String) {
        this.userId = userId
        this.prodId = prodId
        this.amount  = price
        this.description = description
        this.paymentOrderId = "${UUID.randomUUID()}"
    }

    override fun equals(other:Any?): Boolean = kotlinEquals(other, arrayOf(Order::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(Order::id))
    override fun toString(): String = kotlinToString(arrayOf(
        Order::id,
        Order::userId,
        Order::prodId,
        Order::amount,
        Order::txid,
        Order::status,
        Order::createdAt,
        Order::updatedAt
    ))

}