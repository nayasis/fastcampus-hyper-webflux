package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.model.History
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository: CoroutineCrudRepository<History,Long> {
}