package dev.fastcampus.payment.repository

import dev.fastcampus.payment.model.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {
}