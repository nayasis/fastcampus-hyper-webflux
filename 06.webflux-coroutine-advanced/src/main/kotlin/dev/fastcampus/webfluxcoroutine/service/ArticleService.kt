package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.config.CacheKey
import dev.fastcampus.webfluxcoroutine.config.CacheManager
import dev.fastcampus.webfluxcoroutine.config.extension.toLocalDate
import dev.fastcampus.webfluxcoroutine.config.validator.DateString
import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import dev.fastcampus.webfluxcoroutine.exception.NoArticleFound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val dbClient: DatabaseClient,
    private val cache: CacheManager,
) {

    init {
        cache.TTL["/article/get"] = 10.seconds
        cache.TTL["/article/get/all"] = 10.seconds
    }

    suspend fun create(request: ReqCreate): Article {
        return repository.save(request.toArticle())
    }

    suspend fun get(id: Long): Article {
        val key = CacheKey("/article/get", id)
        return cache.get(key) { repository.findById(id) }
            ?: throw NoArticleFound("id: $id")
    }

    suspend fun getAll(title: String? = null): Flow<Article> {
        return if(title.isNullOrBlank()) {
            repository.findAll()
        } else {
            repository.findAllByTitleContains(title)
        }
    }

    suspend fun getAllCached(request: QryArticle): Flow<Article> {
        val key = CacheKey("/article/get/all", request)
        return cache.get(key) {
            getAll(request).toList()
        }?.asFlow() ?: emptyFlow()
    }

    suspend fun getAll(request: QryArticle): Flow<Article> {
        val params = HashMap<String,Any>()
        var sql = dbClient.sql("""
            SELECT id, title, body, author_id, created_at, updated_at
            FROM   TB_ARTICLE
            WHERE  1=1
            ${ request.title.query {
                params["title"] = it.trim().let { "%$it%" }
                "AND title LIKE :title"
            }}
            ${ request.authorId.query {
                params["authorId"] = it
                "AND author_id IN (:authorId)"
            }}
            ${ request.from.query {
                params["from"] = it.toLocalDate()
                "AND created_at >= :from"
            }}
            ${ request.to.query {
                params["to"] = it.toLocalDate().plusDays(1)
            // 2023-01-20 -> 2023-01-21 00:00:00.000
            // <= -> <
                "AND created_at < :to"
            }}
        """.trimIndent())
        params.forEach { key, value -> sql = sql.bind(key,value) }
        return sql.map { row ->
            Article(
                id       = row.get("id") as Long,
                title    = row.get("title") as String,
                body     = row.get("body") as String?,
                authorId = row.get("author_id") as Long,
            ).apply {
                createdAt = row.get("created_at") as LocalDateTime?
                updatedAt = row.get("updated_at") as LocalDateTime?
            }
        }.flow()
    }

    suspend fun update(id: Long, request: ReqUpdate): Article {
        val article = repository.findById(id) ?: throw NoArticleFound("id: $id")
        return repository.save(article.apply {
            request.title?.let { title = it }
            request.body?.let { body = it }
            request.authorId?.let { authorId = it }
        }).also {
            val key = CacheKey("/article/get", id)
            cache.delete(key)
        }
    }

    suspend fun delete(id: Long) {
        return repository.deleteById(id).also {
            val key = CacheKey("/article/get", id)
            cache.delete(key)
        }
    }

}

fun <T> T?.query(f: (T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }
}

data class ReqUpdate (
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ReqCreate (
    val title: String,
    val body: String? = null,
    val authorId: Long? = null,
) {
    fun toArticle(): Article {
        return Article(
            title = this.title,
            body = this.body,
            authorId = this.authorId,
        )
    }
}

data class QryArticle(
    val title: String?,
    val authorId: List<Long>?,
    @DateString
    val from: String?,
    @DateString
    val to: String?,
): Serializable