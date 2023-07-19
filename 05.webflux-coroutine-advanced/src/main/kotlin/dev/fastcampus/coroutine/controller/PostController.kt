package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.model.Post
import dev.fastcampus.coroutine.service.PostService
import dev.fastcampus.coroutine.service.ResPost
import dev.fastcampus.coroutine.service.SavePost
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@RestController
class PostController(
    private val postService: PostService,
) {

//    @GetMapping("/post/all")
//    suspend fun getAll(@RequestParam title: String?): List<Post> {
//        return if(title.isNullOrEmpty()) {
//            postService.getAll()
//        } else {
//            postService.getAll(title)
//        }
//    }

    @GetMapping("/post/all")
    suspend fun getAll(request: QryPost): List<Post> {
        return postService.getAllCached(request)
    }

    @GetMapping("/post/{postId}")
    suspend fun get(@PathVariable postId: Long): Post {
        return postService.get(postId)
    }

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: SavePost): ResPost {
        return postService.create(request)
    }

    @PutMapping("/post/{postId}")
    suspend fun update(@PathVariable postId: Long, @RequestBody request: SavePost, @RequestParam delay: Long?): ResPost {
        return postService.update(postId, request, delay ?: 5000L)
    }

    @DeleteMapping("/post/{postId}")
    suspend fun delete(@PathVariable postId: Long) {
        postService.delete(postId)
    }

}

data class QryPost(
    val title: String?,
    val authorId: Long?,
    val from: String?,
    val to: String?,
): Serializable

//@Configuration
//class Router {
//
//    @Bean
//    fun post(
//        postService: PostService,
//    ): RouterFunction<ServerResponse> {
//        return coRouter { accept(MediaType.APPLICATION_JSON).nest {
//            GET("/post/all") { req ->
//                val title = req.queryParam("title").getOrNull()
//                ok().bodyValueAndAwait(
//                    if(title.isNullOrEmpty()) postService.getAll().toList() else postService.getAll(title).toList()
//                )
//            }
//            GET("/post/{postId}") { req ->
//                val postId = req.pathVariable("postId").toLong()
//                ok().bodyValueAndAwait(
//                    postService.get(postId)
//                )
//            }
//            POST("/post") { req ->
//                status(HttpStatus.CREATED).bodyValueAndAwait(
//                    postService.create(req.awaitBody())
//                )
//            }
//            PUT("/post/{postId}") { req ->
//                val postId = req.pathVariable("postId").toLong()
//                ok().bodyValueAndAwait(
//                    postService.update(postId, req.awaitBody())
//                )
//            }
//            DELETE("/post/{postId}") { req ->
//                val postId = req.pathVariable("postId").toLong()
//                ok().bodyValueAndAwait(
//                    postService.delete(postId)
//                )
//
//            }
//        }}
//    }
//
//}