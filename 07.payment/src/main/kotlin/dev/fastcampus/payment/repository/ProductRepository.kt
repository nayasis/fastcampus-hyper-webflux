package dev.fastcampus.payment.repository

import dev.fastcampus.payment.model.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CoroutineCrudRepository<Product, Long> {
}