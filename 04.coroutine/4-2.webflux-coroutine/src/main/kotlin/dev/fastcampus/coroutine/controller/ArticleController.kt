package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.service.ArticleService
import dev.fastcampus.coroutine.service.ReqCreate
import dev.fastcampus.coroutine.service.ReqUpdate
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(
   @Autowired private val articleService: ArticleService,
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
    suspend fun get(@PathVariable articleId: Long): Article {
        return articleService.get(articleId)
    }

    @GetMapping("/cached/{articleId}")
    suspend fun getCached(@PathVariable articleId: Long): Article {
        return articleService.getCached(articleId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ReqCreate): Article {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    suspend fun update(@PathVariable articleId: Long, @RequestBody request: ReqUpdate): Article {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    suspend fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }

}