package dev.fastcampus.payment.repository

import dev.fastcampus.payment.model.ProductInOrder
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductInOrderRepository: CoroutineCrudRepository<ProductInOrder, Long> {
    suspend fun countByOrderId(orderId: Long): Long
    suspend fun findAllByOrderId(orderId: Long): List<ProductInOrder>
}