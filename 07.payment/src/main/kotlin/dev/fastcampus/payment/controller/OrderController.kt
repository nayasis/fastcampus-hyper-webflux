package dev.fastcampus.payment.controller

import dev.fastcampus.payment.common.Beans.Companion.beanProductInOrderRepository
import dev.fastcampus.payment.common.Beans.Companion.beanProductService
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.PgStatus
import dev.fastcampus.payment.service.OrderHistoryService
import dev.fastcampus.payment.service.OrderService
import dev.fastcampus.payment.service.QryOrderHistory
import dev.fastcampus.payment.service.ReqCreateOrder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val orderHistoryService: OrderHistoryService,
) {

    @GetMapping("/{orderId}")
    suspend fun get(@PathVariable orderId: Long): ResOrder {
        return orderService.get(orderId).toResOrder()
    }

    @GetMapping("/all/{userId}")
    suspend fun getAll(@PathVariable userId: Long): List<ResOrder> {
        return orderService.getAll(userId).map { it.toResOrder() }
    }

    @PostMapping("/create")
    suspend fun create(@RequestBody request: ReqCreateOrder): ResOrder {
        return orderService.create(request).toResOrder()
    }

    @DeleteMapping("/{orderId}")
    suspend fun delete(@PathVariable orderId: Long) {
        orderService.delete(orderId)
    }

    @GetMapping("/history")
    suspend fun getHistories(request: QryOrderHistory): List<Order> {
        return orderHistoryService.getHistories(request)
    }

}

suspend fun Order.toResOrder(): ResOrder {
    return this.let { ResOrder(
        id = it.id,
        userId = it.userId,
        description = it.description,
        amount = it.amount,
        pgOrderId = it.pgOrderId,
        pgKey = it.pgKey,
        pgStatus = it.pgStatus,
        pgRetryCount = it.pgRetryCount,
        createdAt = it.createdAt,
        updatedAt = it.updatedAt,
        products = beanProductInOrderRepository.findAllByOrderId(it.id).map { prodInOrd ->
            ResProductQuantity(
                id = prodInOrd.prodId,
                name = beanProductService.get(prodInOrd.prodId)?.name ?: "unknown",
                price = prodInOrd.price,
                quantity = prodInOrd.quantity,
            )
        },
    )}
}

data class ResOrder(
    val id: Long = 0,
    val userId: Long,
    val description: String? = null,
    val amount: Long = 0,
    val pgOrderId: String? = null,
    val pgKey: String? = null,
    val pgStatus: PgStatus = PgStatus.CREATE,
    val pgRetryCount: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val products: List<ResProductQuantity>
)

data class ResProductQuantity(
    val id: Long,
    val name: String,
    val price: Long,
    val quantity: Int,
)
