package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.service.PostService
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
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
@Transactional
@Sql("classpath:db-init/test.sql")
class PostControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val postService: PostService,
) {

    @Test
    fun getAll() {
        mockMvc.get("/post/all") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(3) }
        }
        mockMvc.get("/post/all?title=title 2") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun get() {
        mockMvc.get("/post/1") {
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
            mockMvc.get("/post/999") {
                contentType = APPLICATION_JSON
            }
        }
    }

    @Test
    fun create() {
        mockMvc.post("/post") {
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
        mockMvc.put("/post/1") {
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
        val prevSize = postService.getAll().size
        mockMvc.post("/post") {
            contentType = APPLICATION_JSON
            content = """{"title": "title 4", "body": "blabla 04", "authorId": 5678}"""
        }.andExpect {
            status { isOk() }
        }
        assertEquals(prevSize + 1, postService.getAll().size)
        mockMvc.delete("/post/1") {
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        assertEquals(prevSize, postService.getAll().size)
    }
}