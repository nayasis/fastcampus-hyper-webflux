package dev.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import dev.fastcampus.payment.model.parent.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.annotation.processing.Generated

@Table("TB_PURCHASE_HISTORY")
class PurchaseHistory(
    @Id
    @Generated
    var id: Long = 0,
    var userId: Long,
    var prodId: Long,
    var description: String? = null,
    var amount: Long = 0,
    var orderId: Long = 0,
): BaseEntity() {

    constructor(order: Order): this(
        userId = order.id,
        prodId = order.prodId,
        description = order.description,
        amount = order.amount,
        orderId = order.id
    )

    override fun equals(other:Any?): Boolean = kotlinEquals(other, arrayOf(PurchaseHistory::id))
    override fun hashCode(): Int = kotlinHashCode(arrayOf(PurchaseHistory::id))
    override fun toString(): String = kotlinToString(arrayOf(
        PurchaseHistory::id,
        PurchaseHistory::userId,
        PurchaseHistory::prodId,
        PurchaseHistory::description,
        PurchaseHistory::amount,
        PurchaseHistory::orderId,
    ), superToString = {super.toString()})

}