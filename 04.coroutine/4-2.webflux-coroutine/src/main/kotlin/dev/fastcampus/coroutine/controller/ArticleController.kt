package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.service.ArticleService
import dev.fastcampus.coroutine.service.ResArticle
import dev.fastcampus.coroutine.service.SaveArticle
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

    @GetMapping("/all")
    suspend fun getAll(@RequestParam title: String?): Flow<Article> {
        return if(title.isNullOrEmpty()) {
            articleService.getAll()
        } else {
            articleService.getAll(title)
        }
    }

    @GetMapping("/{articleId}")
    suspend fun get(@PathVariable articleId: Long): ResArticle {
        return articleService.get(articleId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: SaveArticle): ResArticle {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    suspend fun update(@PathVariable articleId: Long, @RequestBody request: SaveArticle): ResArticle {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    suspend fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }

}