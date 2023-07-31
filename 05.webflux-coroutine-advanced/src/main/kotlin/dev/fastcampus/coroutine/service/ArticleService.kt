package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.config.cache.CoroutineValueOperations
import dev.fastcampus.coroutine.config.lock.Locker
import dev.fastcampus.coroutine.controller.QryArticle
import dev.fastcampus.coroutine.exception.NotFoundException
import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.repository.ArticleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes

private val logger = KotlinLogging.logger {}

private val CACHE_PREFIX = ArticleService::class.simpleName

@Service
class ArticleService(
    private val repository: ArticleRepository,
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
    private val dbclient: DatabaseClient,
    private val locker: Locker,
) {

    private val cache: CoroutineValueOperations
        get() = CoroutineValueOperations(redisTemplate.opsForValue())

    suspend fun getAll(): List<Article> {
        val key = SimpleKey(CACHE_PREFIX, "")
        return cache.get<List<Article>>(key).ifNull {
            logger.debug { "> no cache hit" }
            repository.findAll().toList().also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAll(title: String): List<Article> {
        val key = SimpleKey(CACHE_PREFIX, title)
        return cache.get<List<Article>>(key).ifNull {
            logger.debug { "> no cache hit" }
            repository.findAllByTitleContains(title).also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAllCached(request: QryArticle): List<ResArticle> {
        val key = SimpleKey(CACHE_PREFIX, request)
        return cache.get<List<ResArticle>>(key).ifNull {
            logger.debug { "> no cache hit" }
            getAll(request).map { ResArticle(it) }.also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAll(request: QryArticle): List<Article> {

        val param = HashMap<String, Any>()

        var sql = dbclient.sql("""
            SELECT  *
            FROM    TB_ARTICLE
            WHERE   1=1
            ${request.title.sql {
                param[QryArticle::title.name] = "%$it%"
                "AND  title LIKE :title"
            }}
            ${request.authorId.sql {
                param[QryArticle::authorId.name] = it
                "AND  author_id = :authorId"
            }}
            ${request.from.sql {
                param[QryArticle::from.name] = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
                "AND  created_at >= :from"
            }}
            ${request.to.sql {
                param[QryArticle::to.name] = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1L).atStartOfDay()
                "AND  created_at <= :to"
            }}
        """.trimIndent())

        param.forEach { (key, value) -> sql = sql.bind(key, value) }

        return sql.map { row, _ ->
            Article().apply {
                this.id = row.get("id", Long::class.java)!!
                this.title = row.get("title", String::class.java)
                this.body = row.get("body", String::class.java)
                this.authorId = row.get("author_id", Long::class.java)
                this.createdAt = row.get("created_at", LocalDateTime::class.java)
                this.updatedAt = row.get("updated_at", LocalDateTime::class.java)
            }
        }.flow().toList()

    }

    suspend fun get(articleId: Long): ResArticle {
        return repository.findById(articleId)?.let { ResArticle(it) } ?: throw NotFoundException("article id : $articleId")
    }

    @Transactional
    suspend fun create(request: SaveArticle): ResArticle {
        return repository.save(Article().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).let {
            if(it.title == "error") {
                throw RuntimeException("error")
            }
            ResArticle(it)
        }
    }

    @Transactional
    suspend fun update(articleId: Long, request: SaveArticle, delay: Long = 5000): ResArticle {
        return locker.lock(articleId) {
            repository.findById(articleId)?.let { article ->
                request.title?.let { article.title = it }
                request.body?.let { article.body = it }
                request.authorId?.let { article.authorId = it }
                delay(delay)
                repository.save(article).let { ResArticle(it) }
            } ?: throw NotFoundException("No article(id:$articleId) found")
        }
    }

    @Transactional
    suspend fun delete(articleId: Long) {
        repository.deleteById(articleId)
    }

}

private fun <T> T?.sql(function: (value: T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isEmpty() -> ""
        else -> function.invoke(this)
    }
}

inline fun <T> T?.ifNull(function: () -> T): T {
    return this ?: function.invoke()
}

data class SaveArticle(
    var title: String? = null,
    var body: String? = null,
    var authorId: Long? = null,
)

data class ResArticle(
    var id: Long,
    var title: String,
    var body: String,
    var authorId: Long,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    var version: Int,
) {
    constructor(article: Article): this(
        id = article.id,
        title = article.title ?: "",
        body = article.body ?: "",
        authorId = article.authorId ?: 0,
        createdAt = article.createdAt,
        updatedAt = article.updatedAt,
        version = article.version,
    )
}