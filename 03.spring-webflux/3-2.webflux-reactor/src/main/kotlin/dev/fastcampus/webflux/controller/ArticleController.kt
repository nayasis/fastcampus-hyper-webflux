package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.service.ArticleService
import dev.fastcampus.webflux.service.ReqCreate
import dev.fastcampus.webflux.service.ReqUpdate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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

    @GetMapping("/cached/{articleId}")
    fun getCached(@PathVariable articleId: Long): Mono<Article> {
        return articleService.getCached(articleId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ReqCreate): Mono<Article> {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    fun update(@PathVariable articleId: Long, @RequestBody request: ReqUpdate): Mono<Article> {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    fun delete(@PathVariable articleId: Long): Mono<Void> {
        return articleService.delete(articleId)
    }

}