package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import dev.fastcampus.webflux.model.Post
import dev.fastcampus.webflux.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class PostService(
    private val postRepository: PostRepository,
) {

    fun getAll(): Flux<Post> {
        return postRepository.findAll()
    }

    fun getAll(title: String): Flux<Post> {
        return postRepository.findAllByTitleContains(title)
    }

    fun get(postId: Long): Mono<Post> {
        return postRepository.findById(postId)
            .switchIfEmpty { throw NotFoundException("No post(id:$postId) found") }
    }

    @Transactional
    fun create(request: SavePost): Mono<ResPost> {
        return postRepository.save(Post().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).flatMap {
            if(it.title == "error") {
                Mono.error(RuntimeException("error"))
            } else {
                Mono.just(it)
            }
        }.map { ResPost(it) }
    }

    @Transactional
    fun update(postId: Long, request: SavePost): Mono<ResPost> {
        return postRepository.findById(postId)
            .switchIfEmpty { throw NotFoundException("No post(id:$postId) found") }
            .flatMap {
                postRepository.save(it.apply {
                    if(! request.title.isNullOrEmpty()) it.title = request.title
                    if(! request.body.isNullOrEmpty()) it.body = request.body
                    if(request.authorId != null) it.authorId = request.authorId
                })
            }.map { ResPost(it) }
    }

    @Transactional
    fun delete(postId: Long): Mono<Void> {
        return postRepository.deleteById(postId)
    }

}

data class SavePost(
    val title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ResPost(
    val id: Long,
    var title: String?,
    var body: String?,
    var authorId: Long?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    constructor(post: Post): this(
        post.id,
        post.title,
        post.body,
        post.authorId,
        post.createdAt,
        post.updatedAt,
    )
}
