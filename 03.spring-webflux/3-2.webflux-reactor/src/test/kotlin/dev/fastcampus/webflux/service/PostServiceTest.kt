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
import org.springframework.web.server.ServerWebExchange

private val logger = KotlinLogging.logger{}

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class PostServiceTest(
    @Autowired private val postService: PostService,
    private val transactionOperator: TransactionalOperator
) {

    @Test
    fun getAll() {
        postService.getAll().collectList().doOnNext { assertEquals(3, it.size) }.block()
        postService.getAll("2").collectList().doOnNext { assertEquals(1, it.size) }.block()
    }

    @Test
    fun get() {
        postService.get(1).doOnNext {
            assertEquals("title 1", it.title)
            assertEquals("blabla 01", it.body)
            assertEquals(1234, it.authorId)
        }.block()
        assertThrows<NotFoundException> {
            postService.get(-1).block()
        }
    }

    @Test
    fun create() {
        val request = SavePost("title 4", "blabla 04", 1234)
        postService.create(request).doOnNext {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }.rollback().block()
    }

    @Test
    @Disabled
    fun delete() {
        val prevSize = getPostSize()
        val new = postService.create(SavePost("title 4", "blabla 04", 1234)).toFuture().get()
        assertEquals(prevSize + 1, getPostSize())
        postService.delete(new.id).toFuture().get()
        assertEquals(prevSize, getPostSize())
    }

    private fun getPostSize(): Int = postService.getAll().collectList().map { it.size }.toFuture().get() ?: 0

    @Test
    fun deleteInRollback() {
        postService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            postService.create(SavePost("title 4", "blabla 04", 1234)).flatMap { new ->
                postService.getAll().collectList().map { it.size }.flatMap {
                    assertEquals(prevSize + 1, it)
                    postService.delete(new.id).thenReturn(true).flatMap {
                        postService.getAll().collectList().map { it.size }.doOnNext {
                            assertEquals(prevSize, it)
                        }
                    }
                }
            }
        }.rollback().block()
    }

    @Test
    fun deleteInRollbackInFunctional() {
        postService.getAll().collectList().map { it.size }.flatMap { prevSize ->
            postService.create(SavePost("title 4", "blabla 04", 1234)).zipWhen {
                postService.getAll().collectList().map { it.size }
            }.flatMap {
                val (created, currSize) = it.t1 to it.t2
                assertEquals(prevSize + 1, currSize)
                postService.delete(created.id).thenReturn(true).zipWhen {
                    postService.getAll().collectList().map { it.size }
                }.map {
                    val currSize = it.t2
                    assertEquals(prevSize, currSize)
                }
            }
        }.rollback().block()
    }



}