package dev.fastcampus.mvc.repository

import dev.fastcampus.mvc.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: JpaRepository<Post, Long> {

    fun findAllByTitleContains(title: String): List<Post>

}