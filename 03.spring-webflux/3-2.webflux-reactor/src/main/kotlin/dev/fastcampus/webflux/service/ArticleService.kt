package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    fun getAll(): Flux<Article> {
        return articleRepository.findAll()
    }

    fun getAll(title: String): Flux<Article> {
        return articleRepository.findAllByTitleContains(title)
    }

    fun get(articleId: Long): Mono<Article> {
        return articleRepository.findById(articleId)
            .switchIfEmpty { throw NotFoundException("No article(id:$articleId) found") }
    }

    @Transactional
    fun create(request: SaveArticle): Mono<ResArticle> {
        return articleRepository.save(Article().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).flatMap {
            if(it.title == "error") {
                Mono.error(RuntimeException("error"))
            } else {
                Mono.just(it)
            }
        }.map { ResArticle(it) }
    }

    @Transactional
    fun update(articleId: Long, request: SaveArticle): Mono<ResArticle> {
        return articleRepository.findById(articleId)
            .switchIfEmpty { throw NotFoundException("No article(id:$articleId) found") }
            .flatMap {
                articleRepository.save(it.apply {
                    if(! request.title.isNullOrEmpty()) it.title = request.title
                    if(! request.body.isNullOrEmpty()) it.body = request.body
                    if(request.authorId != null) it.authorId = request.authorId
                })
            }.map { ResArticle(it) }
    }

    @Transactional
    fun delete(articleId: Long): Mono<Void> {
        return articleRepository.deleteById(articleId)
    }

}

data class SaveArticle(
    val title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ResArticle(
    val id: Long,
    var title: String?,
    var body: String?,
    var authorId: Long?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    constructor(article: Article): this(
        article.id,
        article.title,
        article.body,
        article.authorId,
        article.createdAt,
        article.updatedAt,
    )
}
