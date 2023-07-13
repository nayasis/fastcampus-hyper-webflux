package dev.fastcampus.mvc.service

import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@Transactional
@Sql("classpath:db-init/test.sql")
class PostServiceTest(
    @Autowired private val postService: PostService
) {

    @Test
    fun getAll() {
        assertEquals(3, postService.getAll().size)
        assertEquals(1, postService.getAll("2").size)
    }

    @Test
    fun get() {
        postService.get(1).let {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }
        assertThrows<Throwable> {
            postService.get(-1)
        }
    }

    @Test
    fun create() {
        val request = SavePost("title 4", "blabla 04", 1234)
        postService.create(request).let {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }
    }

    @Test
    fun update() {
        val newAuthorId = 999_999L
        postService.update(1, SavePost(authorId = newAuthorId)).let {
            assertEquals(newAuthorId, it.authorId)
        }
    }

    @Test
    fun delete() {
        val prevSize = postService.getAll().size
        val new = postService.create(SavePost("title 4", "blabla 04", 1234))
        assertEquals(prevSize + 1, postService.getAll().size)
        postService.delete(new.id)
        assertEquals(prevSize, postService.getAll().size)
    }

}