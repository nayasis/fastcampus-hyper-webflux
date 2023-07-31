package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.service.ArticleService
import dev.fastcampus.coroutine.service.ResArticle
import dev.fastcampus.coroutine.service.SaveArticle
import mu.KotlinLogging
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
import java.io.Serializable

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

//    @GetMapping("/article/all")
//    suspend fun getAll(@RequestParam title: String?): List<Post> {
//        return if(title.isNullOrEmpty()) {
//            articleService.getAll()
//        } else {
//            articleService.getAll(title)
//        }
//    }

    @GetMapping("/all")
    suspend fun getAll(request: QryArticle): List<ResArticle> {
        return articleService.getAllCached(request)
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
    suspend fun update(@PathVariable articleId: Long, @RequestBody request: SaveArticle, @RequestParam delay: Long?): ResArticle {
        return articleService.update(articleId, request, delay ?: 5000L)
    }

    @DeleteMapping("/{articleId}")
    suspend fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }

}

data class QryArticle(
    val title: String?,
    val authorId: Long?,
    val from: String?,
    val to: String?,
): Serializable

//@Configuration
//class Router {
//
//    @Bean
//    fun article(
//        articleService: ArticleService,
//    ): RouterFunction<ServerResponse> {
//        return coRouter { accept(MediaType.APPLICATION_JSON).nest {
//            GET("/article/all") { req ->
//                val title = req.queryParam("title").getOrNull()
//                ok().bodyValueAndAwait(
//                    if(title.isNullOrEmpty()) articleService.getAll().toList() else articleService.getAll(title).toList()
//                )
//            }
//            GET("/article/{articleId}") { req ->
//                val articleId = req.pathVariable("articleId").toLong()
//                ok().bodyValueAndAwait(
//                    articleService.get(articleId)
//                )
//            }
//            POST("/article") { req ->
//                status(HttpStatus.CREATED).bodyValueAndAwait(
//                    articleService.create(req.awaitBody())
//                )
//            }
//            PUT("/article/{articleId}") { req ->
//                val articleId = req.pathVariable("articleId").toLong()
//                ok().bodyValueAndAwait(
//                    articleService.update(articleId, req.awaitBody())
//                )
//            }
//            DELETE("/article/{articleId}") { req ->
//                val articleId = req.pathVariable("articleId").toLong()
//                ok().bodyValueAndAwait(
//                    articleService.delete(articleId)
//                )
//
//            }
//        }}
//    }
//
//}