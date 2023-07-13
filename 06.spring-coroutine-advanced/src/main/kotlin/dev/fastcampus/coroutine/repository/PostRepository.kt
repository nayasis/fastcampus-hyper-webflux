package dev.fastcampus.coroutine.repository

import dev.fastcampus.coroutine.model.Post
import kotlinx.coroutines.flow.Flow
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: CoroutineCrudRepository<Post,Long> {

    suspend fun findAllByTitleContains(title: String): List<Post>

//    @Lock(LockMode.PESSIMISTIC_WRITE)
    override suspend fun findById(id: Long): Post?

}