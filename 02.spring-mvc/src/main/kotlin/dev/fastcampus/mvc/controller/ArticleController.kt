package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.service.ArticleService
import dev.fastcampus.mvc.service.ResArticle
import dev.fastcampus.mvc.service.SaveArticle
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping("/all")
    fun getAll(@RequestParam title: String?): List<Article> {
        return if(title.isNullOrEmpty()) {
            articleService.getAll()
        } else {
            articleService.getAll(title)
        }
    }

    @GetMapping("/{articleId}")
    fun get(@PathVariable articleId: Long): ResArticle {
        return articleService.get(articleId)
    }

    @PostMapping
    fun create(@RequestBody request: SaveArticle): ResArticle {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    fun update(@PathVariable articleId: Long, @RequestBody request: SaveArticle): ResArticle {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }


}