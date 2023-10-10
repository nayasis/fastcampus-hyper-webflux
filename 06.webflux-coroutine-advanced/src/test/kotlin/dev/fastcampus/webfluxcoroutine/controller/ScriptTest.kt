package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ScriptTest(
    @Autowired private val client: DatabaseClient,
    @Autowired private val repository: ArticleRepository,
): StringSpec({

    beforeSpec {
        val script = ClassPathResource("test.sql")
        client.inConnection { conn ->
            ScriptUtils.executeSqlScript(conn, script)
        }.subscribe()
    }

    "check script" {
        repository.count() shouldBe 3
    }

})