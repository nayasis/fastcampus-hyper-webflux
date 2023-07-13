package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.config.cache.CoroutineValueOperations
import dev.fastcampus.coroutine.config.lock.Locker
import dev.fastcampus.coroutine.controller.QryPost
import dev.fastcampus.coroutine.exception.NotFoundException
import dev.fastcampus.coroutine.model.Post
import dev.fastcampus.coroutine.repository.PostRepository
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

private val CACHE_PREFIX = PostService::class.simpleName

@Service
class PostService(
    private val repository: PostRepository,
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
    private val dbclient: DatabaseClient,
    private val locker: Locker,
) {

    private val cache: CoroutineValueOperations
        get() = CoroutineValueOperations(redisTemplate.opsForValue())

    suspend fun getAll(): List<Post> {
        val key = SimpleKey(CACHE_PREFIX, "")
        return cache.get<List<Post>>(key).ifNull {
            logger.debug { "> no cache hit" }
            repository.findAll().toList().also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAll(title: String): List<Post> {
        val key = SimpleKey(CACHE_PREFIX, title)
        return cache.get<List<Post>>(key).ifNull {
            logger.debug { "> no cache hit" }
            repository.findAllByTitleContains(title).also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAllCached(request: QryPost): List<Post> {
        val key = SimpleKey(CACHE_PREFIX, request)
        return cache.get<List<Post>>(key).ifNull {
            logger.debug { "> no cache hit" }
            getAll(request).also {
                cache.set(key, it, 10.minutes)
            }
        }
    }

    suspend fun getAll(request: QryPost): List<Post> {

        val param = HashMap<String, Any>()

        var sql = dbclient.sql("""
            SELECT  *
            FROM    TB_POST
            WHERE   1=1
            ${request.title.sql {
                param[QryPost::title.name] = "%$it%"
                "AND  title LIKE :title"
            }}
            ${request.authorId.sql {
                param[QryPost::authorId.name] = it
                "AND  author_id = :authorId"
            }}
            ${request.from.sql {
                param[QryPost::from.name] = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
                "AND  created_at >= :from"
            }}
            ${request.to.sql {
                param[QryPost::to.name] = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1L).atStartOfDay()
                "AND  created_at <= :to"
            }}
        """.trimIndent())

        param.forEach { (key, value) -> sql = sql.bind(key, value) }

        return sql.map { row, _ ->
            Post().apply {
                this.id = row.get("id", Long::class.java)!!
                this.title = row.get("title", String::class.java)
                this.body = row.get("body", String::class.java)
                this.authorId = row.get("author_id", Long::class.java)
                this.createdAt = row.get("created_at", LocalDateTime::class.java)
                this.updatedAt = row.get("updated_at", LocalDateTime::class.java)
            }
        }.flow().toList()

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
    suspend fun update(postId: Long, request: SavePost, delay: Long = 5000): ResPost {
        return locker.lock(postId) {
            repository.findById(postId)?.let { post ->
                request.title?.let { post.title = it }
                request.body?.let { post.body = it }
                request.authorId?.let { post.authorId = it }
                delay(delay)
                repository.save(post).let { ResPost(it) }
            } ?: throw NotFoundException("No post(id:$postId) found")
        }
    }

    @Transactional
    suspend fun delete(postId: Long) {
        repository.deleteById(postId)
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
    var version: Int,
) {
    constructor(post: Post): this(
        id = post.id,
        title = post.title ?: "",
        body = post.body ?: "",
        authorId = post.authorId ?: 0,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        version = post.version,
    )
}