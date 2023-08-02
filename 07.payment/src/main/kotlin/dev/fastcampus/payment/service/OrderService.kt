package dev.fastcampus.payment.service

import dev.fastcampus.payment.exception.NotFoundException
import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.enum.TxStatus
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {

    @Transactional
    suspend fun create(userId: Long, prodId: Long): Order {

        val product = productRepository.findById(prodId) ?: throw NotFoundException("No product(id:$prodId) found")

        return orderRepository.save(Order(
            userId,
            prodId,
            product.price,
            product.name,
        ))

    }

}