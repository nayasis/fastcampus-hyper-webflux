package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.reactive.TransactionalOperator

private val logger = KotlinLogging.logger{}

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    private val transactionOperator: TransactionalOperator
) {

    @Test
    fun getAll() {
        articleService.getAll().collectList().doOnNext { assertEquals(3, it.size) }.block()
        articleService.getAll("2").collectList().doOnNext { assertEquals(1, it.size) }.block()
    }

    @Test
    fun get() {
        articleService.get(1).doOnNext {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }.block()
        assertThrows<NotFoundException> {
            articleService.get(-1).block()
        }
    }

    @Test
    fun create() {
        val request = SaveArticle("title 4", "blabla 04", 1234)
        articleService.create(request).doOnNext {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }.rollback().block()
    }

    @Test
    @Disabled
    fun delete() {
        val prevSize = getArticleSize()
        val new = articleService.create(SaveArticle("title 4", "blabla 04", 1234)).toFuture().get()
        assertEquals(prevSize + 1, getArticleSize())
        articleService.delete(new.id).toFuture().get()
        assertEquals(prevSize, getArticleSize())
    }

    private fun getArticleSize(): Int = articleService.getAll().collectList().map { it.size }.toFuture().get() ?: 0

    @Test
    fun deleteInRollback() {
        articleService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            articleService.create(SaveArticle("title 4", "blabla 04", 1234)).flatMap { new ->
                articleService.getAll().collectList().map { it.size }.flatMap {
                    assertEquals(prevSize + 1, it)
                    articleService.delete(new.id).thenReturn(true).flatMap {
                        articleService.getAll().collectList().map { it.size }.doOnNext {
                            assertEquals(prevSize, it)
                        }
                    }
                }
            }
        }.rollback().block()
    }

    @Test
    fun deleteInRollbackInFunctional() {
        articleService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            articleService.create(SaveArticle("title 4", "blabla 04", 1234)).zipWhen {
                articleService.getAll().collectList().map { it.size }
            }.flatMap {
                val (created, currSize) = it.t1 to it.t2
                assertEquals(prevSize + 1, currSize)
                articleService.delete(created.id).thenReturn(true).zipWhen {
                    articleService.getAll().collectList().map { it.size }
                }.map {
                    val currSize = it.t2
                    assertEquals(prevSize, currSize)
                }
            }
        }.rollback().block()
    }



}