package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.service.ResPost
import dev.fastcampus.webflux.service.SavePost
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext
class PostControllerTest(
    @Autowired private val context: ApplicationContext,
) {

    val client = WebTestClient.bindToApplicationContext(context).build()

    @Test
    @Order(1)
    fun getAll() {
        client.get().uri("/post/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
//            .also {
//                it.returnResult().responseBody?.let { logger.debug { ">> response\n${String(it)}" } }
//            }
//            .consumeWith { logger.debug { it } }
//            .jsonPath("$.length()").value<Int> { logger.debug { it }  }
            .jsonPath("$.length()").isEqualTo(3)

        client.get().uri("/post/all?title=2").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    @Test
    @Order(2)
    fun get() {
        client.get().uri("/post/1").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("title").isEqualTo("title 1")
            .jsonPath("body").isEqualTo("blabla 01")
            .jsonPath("authorId").isEqualTo("1234")
        client.get().uri("/post/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    @Test
    @Order(3)
    fun create() {
        val request = SavePost("test", "it is r2dbc demo", 1234)
        client.post().uri("/post").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title!!)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    @Test
    @Order(4)
    fun update() {
        val request = SavePost(authorId = 999999)
        client.put().uri("/post/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    @Test
    @Order(5)
    fun delete() {
        val prevSize = getPostSize()

        val request = SavePost("test", "it is r2dbc demo", 1234)
        val res = client.post().uri("/post").accept(APPLICATION_JSON).bodyValue(request).exchange().expectBody(ResPost::class.java).returnResult().responseBody!!

        assertEquals(prevSize + 1, getPostSize())

        client.delete().uri("/post/${res.id}").exchange().expectStatus().isOk

        assertEquals(prevSize, getPostSize())

    }

    private fun getPostSize(): Int {
        val ref = object: ParameterizedTypeReference<ArrayList<ResPost>>(){}
        return client.get().uri("/post/all").accept(APPLICATION_JSON).exchange().expectBody(ref).returnResult().responseBody?.size ?: 0
    }
}