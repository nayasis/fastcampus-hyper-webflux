package dev.fastcampus.coroutine.repository

import dev.fastcampus.coroutine.model.Article
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article,Long> {

    suspend fun findAllByTitleContains(title: String): List<Article>

//    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long): Article?

}