package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.model.ArticleDocument
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostDocumentRepository: CoroutineCrudRepository<ArticleDocument, Long> {

    suspend fun findAllByTitleContains(title: String): List<ArticleDocument>

}