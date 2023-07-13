package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.service.ResPost
import dev.fastcampus.coroutine.service.SavePost
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.*
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
class PostControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val client: DatabaseClient,
): StringSpec({

    beforeSpec {
        println(">> initialize db")
        val script = ClassPathResource("db-init/test.sql")
        client.inConnection { connection ->
            ScriptUtils.executeSqlScript(connection, script )
        }.block()
    }

    val client = WebTestClient.bindToApplicationContext(context).build()

    fun getPostSize(): Int {
        val ref = object: ParameterizedTypeReference<ArrayList<ResPost>>(){}
        return client.get().uri("/post/all").accept(APPLICATION_JSON).exchange().expectBody(ref).returnResult().responseBody?.size ?: 0
    }

    "get all" {
        client.get().uri("/post/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
        client.get().uri("/post/all?title=2").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    "get" {
        client.get().uri("/post/1").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("title").isEqualTo("title 1")
            .jsonPath("body").isEqualTo("blabla 01")
            .jsonPath("authorId").isEqualTo("1234")
        client.get().uri("/post/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    "create" {
        val request = SavePost("test", "it is r2dbc demo", 1234)
        client.post().uri("/post").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title!!)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "update" {
        val request = SavePost(authorId = 999999)
        client.put().uri("/post/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "delete" {
        val prevSize = getPostSize()
        val res = client.post().uri("/post").accept(APPLICATION_JSON)
            .bodyValue(SavePost("test", "it is r2dbc demo", 1234)).exchange()
            .expectBody(ResPost::class.java).returnResult().responseBody!!
        assertEquals(prevSize + 1, getPostSize())
        client.delete().uri("/post/${res.id}").exchange().expectStatus().isOk
        assertEquals(prevSize, getPostSize())
    }

})