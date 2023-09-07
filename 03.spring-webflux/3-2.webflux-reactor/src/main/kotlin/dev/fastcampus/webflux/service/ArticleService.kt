package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {

    val ops = redisTemplate.opsForValue()

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

    fun getCached(id: Long): Mono<Article> {
        val key = "reactor/article/${id}"
        return ops.get(key).switchIfEmpty {
            get(id).flatMap {
                ops.set(key,it)
                    .doOnNext { redisTemplate.expire(key, Duration.ofSeconds(120)) }
                    .then(Mono.just(it))
            }
        } as Mono<Article>
    }

    @Transactional
    fun create(request: ReqCreate): Mono<Article> {
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
        }
    }

    @Transactional
    fun update(articleId: Long, request: ReqUpdate): Mono<Article> {
        return articleRepository.findById(articleId)
            .switchIfEmpty { throw NotFoundException("No article(id:$articleId) found") }
            .flatMap { article ->
                request.title?.let { article.title = it }
                request.body?.let { article.body = it }
                request.authorId?.let { article.authorId = it }
                articleRepository.save(article)
            }
    }

    @Transactional
    fun delete(articleId: Long): Mono<Void> {
        return articleRepository.deleteById(articleId)
    }

}

data class ReqCreate(
    val title: String,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ReqUpdate(
    val title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)
