package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.exception.NotFoundException
import dev.fastcampus.coroutine.model.Post
import dev.fastcampus.coroutine.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PostService(
    @Autowired private val repository: PostRepository,
) {

    fun getAll(): Flow<Post> {
        return repository.findAll()
    }

    suspend fun getAll(title: String): Flow<Post> {
        return repository.findAllByTitleContains(title)
    }

    suspend fun get(postId: Long): Post {
        return repository.findById(postId) ?: throw NotFoundException("post id : $postId")
    }

    @Transactional
    suspend fun create(request: SavePost): ResPost {
        return repository.save(Post().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).let {
            if(it.title == "error") {
                throw RuntimeException("error")
            }
            ResPost(it)
        }
    }

    @Transactional
    suspend fun update(postId: Long, request: SavePost): ResPost {
        return repository.findById(postId)?.let { post ->
            request.title?.let { post.title = it }
            request.body?.let { post.body = it }
            request.authorId?.let { post.authorId = it }
            repository.save(post).let { ResPost(it) }
        } ?: throw NotFoundException("No post(id:$postId) found")
    }

    @Transactional
    suspend fun delete(postId: Long) {
        repository.deleteById(postId)
    }

}

data class SavePost(
    var title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ResPost(
    var id: Long,
    var title: String,
    var body: String,
    var authorId: Long,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    constructor(post: Post): this(
        id = post.id,
        title = post.title ?: "",
        body = post.body ?: "",
        authorId = post.authorId ?: 0,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
    )
}