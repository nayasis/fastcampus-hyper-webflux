package dev.fastcampus.coroutine.repository

import dev.fastcampus.coroutine.model.Post
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: CoroutineCrudRepository<Post,Long> {

    suspend fun findAllByTitleContains(title: String): Flow<Post>

}