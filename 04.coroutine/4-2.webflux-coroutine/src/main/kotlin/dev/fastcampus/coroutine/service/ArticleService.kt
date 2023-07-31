package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.exception.NotFoundException
import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleService(
    @Autowired private val repository: ArticleRepository,
) {

    fun getAll(): Flow<Article> {
        return repository.findAll()
    }

    suspend fun getAll(title: String): Flow<Article> {
        return repository.findAllByTitleContains(title)
    }

    suspend fun get(articleId: Long): ResArticle {
        return repository.findById(articleId)?.let { ResArticle(it) } ?: throw NotFoundException("post id : $articleId")
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
    suspend fun update(articleId: Long, request: SaveArticle): ResArticle {
        return repository.findById(articleId)?.let { article ->
            request.title?.let { article.title = it }
            request.body?.let { article.body = it }
            request.authorId?.let { article.authorId = it }
            repository.save(article).let { ResArticle(it) }
        } ?: throw NotFoundException("No post(id:$articleId) found")
    }

    @Transactional
    suspend fun delete(articleId: Long) {
        repository.deleteById(articleId)
    }

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
) {
    constructor(article: Article): this(
        id = article.id,
        title = article.title ?: "",
        body = article.body ?: "",
        authorId = article.authorId ?: 0,
        createdAt = article.createdAt,
        updatedAt = article.updatedAt,
    )
}