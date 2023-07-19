package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.model.Post
import dev.fastcampus.webflux.service.PostService
import dev.fastcampus.webflux.service.ResPost
import dev.fastcampus.webflux.service.SavePost
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PostController(
    private val postService: PostService,
) {

    @GetMapping("/post/all")
    fun getAll(@RequestParam title: String?): Flux<Post> {
        return if(title.isNullOrEmpty()) {
            postService.getAll()
        } else {
            postService.getAll(title)
        }
    }

    @GetMapping("/post/{postId}")
    fun get(@PathVariable postId: Long): Mono<Post> {
        return postService.get(postId)
    }

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: SavePost): Mono<ResPost> {
        return postService.create(request)
    }

    @PutMapping("/post/{postId}")
    fun update(@PathVariable postId: Long, @RequestBody request: SavePost): Mono<ResPost> {
        return postService.update(postId, request)
    }

    @DeleteMapping("/post/{postId}")
    fun delete(@PathVariable postId: Long): Mono<Void> {
        return postService.delete(postId)
    }

}