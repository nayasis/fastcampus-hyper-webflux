package dev.fastcampus.payment.service

import dev.fastcampus.payment.exception.NoOrderFound
import dev.fastcampus.payment.exception.NoProductFound
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.PgStatus
import dev.fastcampus.payment.model.ProductInOrder
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.ProductInOrderRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val productInOrderRepository: ProductInOrderRepository,
) {

    @Transactional
    suspend fun create(request: ReqCreateOrder): Order {
        val prodIds = request.products.map { it.prodId }.toSet()
        val productsById = request.products.mapNotNull { productService.get(it.prodId) }.associateBy { it.id }
        prodIds.filter { ! productsById.containsKey(it) }.let { remains ->
            if(remains.isNotEmpty())
                throw NoProductFound("prod ids: $remains")
        }

        val amount = request.products.sumOf { productsById[it.prodId]!!.price * it.quantity }
        val description = request.products.joinToString(", ") { "${productsById[it.prodId]!!.name} x ${it.quantity}" }

        val newOrder = orderRepository.save(Order(
            userId = request.userId,
            description = description,
            amount = amount,
            pgOrderId = "${UUID.randomUUID()}".replace("-",""),
            pgStatus = PgStatus.CREATE,
        ))

        request.products.forEach {
            productInOrderRepository.save( ProductInOrder(
                orderId = newOrder.id,
                prodId = it.prodId,
                price = productsById[it.prodId]!!.price,
                quantity = it.quantity,
            ))
        }
        return newOrder
    }

    suspend fun get(orderId: Long): Order {
        return orderRepository.findById(orderId) ?: throw NoOrderFound("id: $orderId")
    }

    suspend fun getAll(userId: Long): List<Order> {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
    }

    suspend fun delete(orderId: Long) {
        orderRepository.deleteById(orderId)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun save(order: Order) {
        orderRepository.save(order)
    }

    suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?:
        throw NoOrderFound("pgOrderId: $pgOrderId")
    }

}

data class ReqCreateOrder(
    val userId: Long,
    var products: List<ReqProdQuantity>,
)

data class ReqProdQuantity(
    val prodId: Long,
    val quantity: Int,
)
