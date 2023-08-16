package dev.fastcampus.mvc.service

import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("classpath:db-init/test.sql")
@ExtendWith(MockitoExtension::class)
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
//    @Mock private val articleService: ArticleService,
) {

    @Test
    fun getAll() {
//        Mockito.`when`(articleService.getAll()).thenReturn(listOf(
//            Article(1,"title1","body1",1234),
//            Article(2,"title2","body2",1234),
//            Article(3,"title3","body3",1234),
//        ))
//        Mockito.`when`(articleService.getAll("2")).thenReturn(listOf(
//            Article(2,"title2","body2",1234),
//        ))
        assertEquals(3, articleService.getAll().size)
        assertEquals(1, articleService.getAll("2").size)
    }

    @Test
    fun get() {
        articleService.get(1).let {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }
        assertThrows<Throwable> {
            articleService.get(-1)
        }
    }

    @Test
    fun create() {
        val request = ReqCreate("title 4", "blabla 04", 1234)
        articleService.create(request).let {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }
    }

    @Test
    fun update() {
        val newAuthorId = 999_999L
        articleService.update(1, ReqCreate(authorId = newAuthorId)).let {
            assertEquals(newAuthorId, it.authorId)
        }
    }

    @Test
    fun delete() {
        val prevSize = articleService.getAll().size
        val new = articleService.create(ReqCreate("title 4", "blabla 04", 1234))
        assertEquals(prevSize + 1, articleService.getAll().size)
        articleService.delete(new.id)
        assertEquals(prevSize, articleService.getAll().size)
    }

}