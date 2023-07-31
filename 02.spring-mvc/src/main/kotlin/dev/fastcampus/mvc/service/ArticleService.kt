package dev.fastcampus.mvc.service

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.repository.ArticleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ArticleService(
    private val repository: ArticleRepository
) {

    fun getAll(): List<Article> {
        return repository.findAll()
    }

    fun getAll(title: String): List<Article> {
        return repository.findAllByTitleContains(title)
    }

    fun get(articleId: Long): ResArticle {
        return repository.findByIdOrNull(articleId)?.let { ResArticle(it) }
            ?: throw NoSuchElementException("article id : $articleId")
    }

    @Transactional
    fun create(request: SaveArticle): ResArticle {
        return repository.save(Article().apply {
            title = request.title
            body = request.body
            authorId = request.authorId
        }).let { ResArticle(it) }
    }

    @Transactional
    fun update(articleId: Long, request: SaveArticle): ResArticle {
        return repository.findByIdOrNull(articleId)?.let{ article ->
            article.title = request.title
            article.body = request.body
            article.authorId = request.authorId
            repository.save(article).let { ResArticle(it) }
        } ?: throw NoSuchElementException("article id : $articleId")
    }

    @Transactional
    fun delete(articleId: Long) {
        repository.deleteById(articleId)
    }

}

data class SaveArticle(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ResArticle (
    val id: Long,
    val title: String?,
    val body: String?,
    val authorId: Long?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    constructor(article: Article): this(
        id = article.id,
        title = article.title,
        body = article.body,
        authorId = article.authorId,
        createdAt = article.createdAt,
        updatedAt = article.updatedAt,
    )
}