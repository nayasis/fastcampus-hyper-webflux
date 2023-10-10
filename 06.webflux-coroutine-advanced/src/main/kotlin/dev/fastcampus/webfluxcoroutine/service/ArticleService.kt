package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.config.CacheKey
import dev.fastcampus.webfluxcoroutine.config.CacheManager
import dev.fastcampus.webfluxcoroutine.config.extension.toLocalDate
import dev.fastcampus.webfluxcoroutine.exception.NoArticleFound
import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val dbClient: DatabaseClient,
//    redisTemplate: ReactiveRedisTemplate<Any,Any>,
    private val cache: CacheManager,
) {

//    private val ops = redisTemplate.opsForValue()

    suspend fun create(request: ReqCreate): Article {
        return repository.save(request.toArticle())
    }

//    suspend fun get(id: Long): Article {
//        val key = SimpleKey("/article/get", id)
//        return ops.get(key).awaitSingleOrNull()?.let { it as Article }
//            ?: repository.findById(id)?.also { ops.set(key,it,10.seconds.toJavaDuration()).awaitSingle() }
//            ?: throw NoArticleFound("id: $id")
//    }

    val CACHE_GET    = "/article/get"
    val CACHE_GETALL = "/article/get/all"

    @PostConstruct
    fun init() {
        cache.ttl[CACHE_GET] = 10.seconds
        cache.ttl[CACHE_GETALL] = 10.seconds
    }

//    suspend fun get(id: Long): Article {
//        val key = CacheKey(CACHE_GET,id)
//        return cache.get(key)
//            ?: repository.findById(id)?.also { cache.set(key,it) }
//            ?: throw NoArticleFound("id: $id")
//    }

    suspend fun get(id: Long): Article {
        val key = CacheKey(CACHE_GET,id)
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
        val key = CacheKey(CACHE_GETALL,request)
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
            ${request.title.query {
                params["title"] = it.trim().let { "%$it%" }
                "AND    title LIKE :title"                    
            }}
            ${request.authorId.query {
                params["authorId"] = it
                "AND    author_id IN (:authorId)"
            }}                        
            ${request.from.query {
                params["from"] = it.toLocalDate()
                "AND    created_at >= :from"
            }}
            ${request.to.query {
                params["to"] = it.toLocalDate().plusDays(1)
                "AND    created_at < :to"
            }}
        """.trimIndent())
        params.forEach { (key, value) -> sql = sql.bind(key,value) }
        return sql.map { row ->
            Article(
                id = row.get("id") as Long,
                title = row.get("title") as String,
                body = row.get("body") as String?,
                authorId = row.get("author_id") as Long?,
            ).apply {
                createdAt = row.get("created_at") as LocalDateTime?
                updatedAt = row.get("updated_at") as LocalDateTime?
            }
        }.flow()
    }

//    suspend fun update(id: Long, request: ReqUpdate): Article {
//        val key = SimpleKey("/article/get", id)
//        val article = repository.findById(id) ?: throw NoArticleFound("id: $id")
//        return repository.save(article.apply {
//            request.title?.let { title = it }
//            request.body?.let { body = it }
//            request.authorId?.let { authorId = it }
//        }).also { ops.delete(key).awaitSingle() }
//    }

    suspend fun update(id: Long, request: ReqUpdate): Article {
        val article = repository.findById(id) ?: throw NoArticleFound("id: $id")
        return repository.save(article.apply {
            request.title?.let { title = it }
            request.body?.let { body = it }
            request.authorId?.let { authorId = it }
        }).also { cache.delete(CacheKey(CACHE_GET, id)) }
    }

//    suspend fun delete(id: Long) {
//        val key = SimpleKey("/article/get", id)
//        return repository.deleteById(id)
//            .also { ops.delete(key).awaitSingle() }
//    }

    suspend fun delete(id: Long) {
        return repository.deleteById(id)
            .also { cache.delete(CacheKey(CACHE_GET, id)) }
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

fun <T> T?.query(f: (T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        else -> f.invoke(this)
    }
//    return if(this == null) "" else f.invoke()
}

data class QryArticle (
    val title: String?,
    val authorId: List<Long>?,
    val from: String?,
    val to: String?,
): Serializable