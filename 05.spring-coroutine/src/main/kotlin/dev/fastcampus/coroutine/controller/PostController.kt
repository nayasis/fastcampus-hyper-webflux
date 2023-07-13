package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.model.Post
import dev.fastcampus.coroutine.service.PostService
import dev.fastcampus.coroutine.service.ResPost
import dev.fastcampus.coroutine.service.SavePost
import kotlinx.coroutines.flow.Flow
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

@RestController
class PostController(
    private val postService: PostService,
) {

    @GetMapping("/post/all")
    suspend fun getAll(@RequestParam title: String?): Flow<Post> {
        return if(title.isNullOrEmpty()) {
            postService.getAll()
        } else {
            postService.getAll(title)
        }
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
    suspend fun update(@PathVariable postId: Long, @RequestBody request: SavePost): ResPost {
        return postService.update(postId, request)
    }

    @DeleteMapping("/post/{postId}")
    suspend fun delete(@PathVariable postId: Long) {
        postService.delete(postId)
    }

}