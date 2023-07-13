package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.model.PostDocument
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostDocumentRepository: CoroutineCrudRepository<PostDocument, Long> {

    suspend fun findAllByTitleContains(title: String): List<PostDocument>

}