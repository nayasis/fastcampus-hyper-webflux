package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import dev.fastcampus.payment.common.Beans
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.model.parent.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import javax.annotation.processing.Generated

@Table("TB_ORDER")
class Order(
    @Id
    @Generated
    var id: Long = 0,
    var userId: Long,
    var prodId: Long,
    var description: String? = null,
    var amount: Long = 0,
    var txid: String? = null,
    var status: TxStatus = TxStatus.CREATE,
    var paymentOrderId: String? = "${UUID.randomUUID()}",

): BaseEntity() {

    suspend fun getProduct(): Product? {
        return prodId?.let { Beans.productRepository.findById(it) }
    }

    constructor(userId: Long, prodId: Long, price: Long, description: String): this(
        userId = userId,
        prodId = prodId,
        description = description,
        amount = price,
    )

    override fun equals(other:Any?): Boolean = kotlinEquals(other, arrayOf(Order::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(Order::id))
    override fun toString(): String = kotlinToString(arrayOf(
        Order::id,
        Order::userId,
        Order::prodId,
        Order::description,
        Order::amount,
        Order::txid,
        Order::status,
    ), superToString = {super.toString()})

}

