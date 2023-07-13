package dev.fastcampus.mvc.service

import dev.fastcampus.mvc.model.Post
import dev.fastcampus.mvc.repository.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PostService(
    private val repository: PostRepository
) {

    fun getAll(): List<Post> {
        return repository.findAll()
    }

    fun getAll(title: String): List<Post> {
        return repository.findAllByTitleContains(title)
    }

    fun get(postId: Long): ResPost {
        return repository.findByIdOrNull(postId)?.let { ResPost(it) }
            ?: throw NoSuchElementException("post id : $postId")
    }

    @Transactional
    fun create(request: SavePost): ResPost {
        return repository.save(Post().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).let { ResPost(it) }
    }

    @Transactional
    fun update(postId: Long, request: SavePost): ResPost {
        return repository.findByIdOrNull(postId)?.let{ post ->
            post.title = request.title
            post.body = request.body
            post.authorId = request.authorId
            repository.save(post).let { ResPost(it) }
        } ?: throw NoSuchElementException("post id : $postId")
    }

    @Transactional
    fun delete(postId: Long) {
        repository.deleteById(postId)
    }

}

data class SavePost(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ResPost (
    val id: Long,
    val title: String?,
    val body: String?,
    val authorId: Long?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    constructor(post: Post): this(
        id = post.id,
        title = post.title,
        body = post.body,
        authorId = post.authorId,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
    )
}