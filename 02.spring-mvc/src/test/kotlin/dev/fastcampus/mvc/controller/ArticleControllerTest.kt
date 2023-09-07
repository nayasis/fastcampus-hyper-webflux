package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.repository.ArticleRepository
import dev.fastcampus.mvc.service.ArticleService
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql("classpath:db-init/test.sql")
class ArticleControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val articleService: ArticleService,
    @Autowired private val repository: ArticleRepository,
) {

    @Test
    fun getAll() {
        mockMvc.get("/article/all") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(3) }
        }
        mockMvc.get("/article/all?title=title 2") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun get() {
        mockMvc.get("/article/1") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("title") { value("title 1") }
            jsonPath("body") { value("blabla 01") }
            jsonPath("authorId") { value("1234") }
        }

        // MockMvc 는 에러메세지 테스트가 안된다.
        // Spring MVC는 Servlet container 오류매핑을 기반으로 error를 처리하고 있으나,
        // MockMvc는 containerless 테스트라서 error 처리로직을 타지 않는다.
        // https://github.com/spring-projects/spring-boot/issues/7321
        assertThrows<Exception> {
            mockMvc.get("/article/999") {
                contentType = APPLICATION_JSON
            }
        }
    }

    @Test
    fun create() {
        mockMvc.post("/article") {
            contentType = APPLICATION_JSON
            content = """
                {"title": "title 4", "body": "blabla 04", "authorId": 5678}
                """.trimIndent()
        }.andExpect {
            status { isOk() }
            jsonPath("id") { value("4") }
            jsonPath("title") { value("title 4") }
            jsonPath("body") { value("blabla 04") }
            jsonPath("authorId") { value("5678") }
        }
    }

    @Test
    fun update() {
        mockMvc.put("/article/1") {
            contentType = APPLICATION_JSON
            content = """
                {"authorId": 999999}
                """.trimIndent()
        }.andExpect {
            status { isOk() }
            jsonPath("authorId") { value("999999") }
        }
    }

    @Test
    fun delete() {
        val prevSize = articleService.getAll().size
        mockMvc.post("/article") {
            contentType = APPLICATION_JSON
            content = """{"title": "title 4", "body": "blabla 04", "authorId": 5678}"""
        }.andExpect {
            status { isOk() }
        }
        assertEquals(prevSize + 1, repository.count())
        mockMvc.delete("/article/1") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        assertEquals(prevSize, repository.count())
    }
}