package dev.fastcampus.webflux.repository

import dev.fastcampus.webflux.model.Post
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PostRepository: R2dbcRepository<Post,Long> {

    fun findAllByTitleContains(title: String): Flux<Post>

}