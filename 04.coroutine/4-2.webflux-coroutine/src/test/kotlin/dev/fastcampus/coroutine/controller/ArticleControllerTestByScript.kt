package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.model.Article
import dev.fastcampus.coroutine.service.ReqCreate
import dev.fastcampus.coroutine.service.ReqUpdate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
class ArticleControllerTestByScript(
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

})