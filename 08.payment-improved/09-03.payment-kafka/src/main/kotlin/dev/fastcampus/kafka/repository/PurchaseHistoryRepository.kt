package dev.fastcampus.kafka.repository

import dev.fastcampus.kafka.model.PurchaseHistory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseHistoryRepository: CoroutineCrudRepository<PurchaseHistory, Long>