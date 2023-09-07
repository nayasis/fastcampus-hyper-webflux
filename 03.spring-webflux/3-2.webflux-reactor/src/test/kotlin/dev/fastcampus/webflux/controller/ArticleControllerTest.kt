package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import dev.fastcampus.webflux.service.ReqCreate
import dev.fastcampus.webflux.service.ReqUpdate
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
//@DirtiesContext
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val repository: ArticleRepository,
) {

    val client = WebTestClient.bindToApplicationContext(context).build()

    @Test
    @Order(1)
    fun create() {
        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    @Test
    @Order(2)
    fun getAll() {
        client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
//            .also {
//                it.returnResult().responseBody?.let { logger.debug { ">> response\n${String(it)}" } }
//            }
//            .consumeWith { logger.debug { it } }
//            .jsonPath("$.length()").value<Int> { logger.debug { it }  }
            .jsonPath("$.length()").isEqualTo(1)

        client.get().uri("/article/all?title=a").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("$.length()").isEqualTo(0)
        client.get().uri("/article/all?title=te").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    @Test
    @Order(2)
    fun get() {

        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        val id = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(Article::class.java).returnResult().responseBody!!.id

        val res = client.get().uri("/article/${id}").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody(Article::class.java).returnResult().responseBody!!

        assertEquals(request.title, res.title)
        assertEquals(request.body, res.body)
        assertEquals(request.authorId, res.authorId)

        client.get().uri("/article/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    @Test
    @Order(4)
    fun update() {
        val request = ReqUpdate(authorId = 999999)
        client.put().uri("/article/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    @Test
    @Order(5)
    fun delete() {
        val prevSize = getArticleSize()

        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        val res = client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange().expectBody(Article::class.java).returnResult().responseBody!!

        assertEquals(prevSize + 1, getArticleSize())

        client.delete().uri("/article/${res.id}").exchange().expectStatus().isOk

        assertEquals(prevSize, getArticleSize())

    }

    private fun getArticleSize(): Long {
//        val ref = object: ParameterizedTypeReference<ArrayList<Article>>(){}
//        return client.get().uri("/article/all").accept(APPLICATION_JSON).exchange().expectBody(ref).returnResult().responseBody?.size ?: 0
        return repository.count().block() ?: 0
    }
}