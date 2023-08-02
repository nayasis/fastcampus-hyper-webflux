package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.service.ArticleService
import dev.fastcampus.webflux.service.ResArticle
import dev.fastcampus.webflux.service.SaveArticle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

    @GetMapping("/all")
    fun getAll(@RequestParam title: String?): Flux<Article> {
        return if(title.isNullOrEmpty()) {
            articleService.getAll()
        } else {
            articleService.getAll(title)
        }
    }

    @GetMapping("/{articleId}")
    fun get(@PathVariable articleId: Long): Mono<Article> {
        return articleService.get(articleId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: SaveArticle): Mono<ResArticle> {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    fun update(@PathVariable articleId: Long, @RequestBody request: SaveArticle): Mono<ResArticle> {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long): Mono<Void> {
        return articleService.delete(articleId)
    }

}