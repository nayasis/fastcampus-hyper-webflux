package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.model.Post
import dev.fastcampus.mvc.service.PostService
import dev.fastcampus.mvc.service.ResPost
import dev.fastcampus.mvc.service.SavePost
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postService: PostService
) {

    @GetMapping("/post/all")
    fun getAll(@RequestParam title: String?): List<Post> {
        return if(title.isNullOrEmpty()) {
            postService.getAll()
        } else {
            postService.getAll(title)
        }
    }

    @GetMapping("/post/{postId}")
    fun get(@PathVariable postId: Long): ResPost {
        return postService.get(postId)
    }

    @PostMapping("/post")
    fun create(@RequestBody request: SavePost): ResPost {
        return postService.create(request)
    }

    @PutMapping("/post/{postId}")
    fun update(@PathVariable postId: Long, @RequestBody request: SavePost): ResPost {
        return postService.update(postId, request)
    }

    @DeleteMapping("/post/{postId}")
    fun delete(@PathVariable postId: Long) {
        postService.delete(postId)
    }


}