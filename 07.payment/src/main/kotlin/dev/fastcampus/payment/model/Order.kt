package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import dev.fastcampus.payment.common.Beans
import dev.fastcampus.payment.model.enum.TxStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.annotation.processing.Generated

@Table("TB_ORDER")
class Order {

    @Id
    @Generated
    var id: Long = 0

    var userId: Long? = null

    var prodId: Long? = null

    var prodPrice: Long = 0

    var billPrice: Long = 0

    val txid: String? = null

    val status: TxStatus = TxStatus.NONE

    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

    suspend fun getProduct(): Product? {
        return prodId?.let { Beans.productRepository.findById(it) }
    }

    constructor(userId: Long?, prodId: Long?, billPrice: Long) {
        this.userId = userId
        this.prodId = prodId
        this.billPrice = billPrice
    }

    override fun equals( other:Any? ): Boolean = kotlinEquals(other, arrayOf(Order::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(Order::id))
    override fun toString(): String {
        return "Order(id=$id, userId=$userId, prodId=$prodId, prodPrice=$prodPrice, billPrice=$billPrice, txid=$txid, status=$status, createdAt=$createdAt, updatedAt=$updatedAt)"
    }

}