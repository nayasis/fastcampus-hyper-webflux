package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.service.ResArticle
import dev.fastcampus.coroutine.service.ReqCreate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
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
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val client: DatabaseClient,
): StringSpec({

    beforeSpec {
        println(">> initialize db")
        val script = ClassPathResource("db-init/test.sql")
        client.inConnection { connection ->
            ScriptUtils.executeSqlScript(connection, script)
        }.subscribe()
    }

    val client = WebTestClient.bindToApplicationContext(context).build()

    fun getArticleSize(): Int {
        val ref = object: ParameterizedTypeReference<ArrayList<ResArticle>>(){}
        return client.get().uri("/article/all").accept(APPLICATION_JSON).exchange().expectBody(ref).returnResult().responseBody?.size ?: 0
    }

    "get all" {
        client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
        client.get().uri("/article/all?title=2").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    "get" {
        client.get().uri("/article/1").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("title").isEqualTo("title 1")
            .jsonPath("body").isEqualTo("blabla 01")
            .jsonPath("authorId").isEqualTo("1234")
        client.get().uri("/article/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    "create" {
        val request = ReqCreate("test", "it is r2dbc demo", 1234)
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title!!)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "update" {
        val request = ReqCreate(authorId = 999999)
        client.put().uri("/article/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "delete" {
        val prevSize = getArticleSize()
        val res = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("test", "it is r2dbc demo", 1234)).exchange()
            .expectBody(ResArticle::class.java).returnResult().responseBody!!
        getArticleSize() shouldBe prevSize + 1
        client.delete().uri("/article/${res.id}").exchange().expectStatus().isOk
        getArticleSize() shouldBe prevSize
    }

})