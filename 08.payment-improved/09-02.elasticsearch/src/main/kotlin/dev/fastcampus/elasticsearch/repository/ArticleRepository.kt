package dev.fastcampus.elasticsearch.repository

import dev.fastcampus.elasticsearch.model.Article
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long> {

    suspend fun findAllByTitleContains(title: String): List<Article>

}