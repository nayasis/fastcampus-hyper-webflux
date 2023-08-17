package dev.fastcampus.payment.service

import dev.fastcampus.payment.exception.NotFoundException
import dev.fastcampus.payment.model.PurchaseHistory
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.PurchaseHistoryRepository
import org.springframework.stereotype.Service

@Service
class PurchaseHistoryService(
    private val purchaseHistoryRepository: PurchaseHistoryRepository,
    private val orderRepository: OrderRepository,
) {

    suspend fun create(orderId: Long): PurchaseHistory {
        val order = orderRepository.findById(orderId) ?: throw NotFoundException("order id : $orderId")
        return purchaseHistoryRepository.save(PurchaseHistory(order))
    }
    
    

}