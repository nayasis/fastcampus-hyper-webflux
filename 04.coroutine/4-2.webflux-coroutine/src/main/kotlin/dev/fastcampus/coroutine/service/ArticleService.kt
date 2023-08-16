package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.exception.NotFoundException
import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {

    private val ops = redisTemplate.opsForValue()

    suspend fun getAll(): Flow<Article> {
        return repository.findAll()
    }

    suspend fun getAll(title: String): Flow<Article> {
        return repository.findAllByTitleContains(title)
    }

    suspend fun get(articleId: Long): Article {
        return repository.findById(articleId) ?: throw NotFoundException("id: $articleId")
    }

    suspend fun getCached(id: Long): Article {
        val key = "coroutine/article/${id}"
        return (ops.get(key).awaitSingleOrNull() as? Article) ?: get(id).also {
            ops.set(key,it).awaitSingle()
            redisTemplate.expire(key, Duration.ofSeconds(120))
        }
    }

    @Transactional
    suspend fun create(request: ReqCreate): Article {
        return repository.save(Article(
            title = request.title,
            body = request.body,
            authorId = request.authorId
        ))
    }

    @Transactional
    suspend fun update(articleId: Long, request: ReqUpdate): Article {
        return repository.findById(articleId)?.let { article ->
            request.title?.let { article.title = it }
            request.body?.let { article.body = it }
            request.authorId?.let { article.authorId = it }
            repository.save(article)
        } ?: throw NotFoundException("id: $articleId")
    }

    @Transactional
    suspend fun delete(articleId: Long) {
        repository.deleteById(articleId)
    }

}

data class ReqCreate(
    var title: String,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ReqUpdate(
    var title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)