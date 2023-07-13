package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.exception.NotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
// Kotest에서 아래 annotation은 작동하지 않는다.
////@Transactional
//@Sql("classpath:db-init/test.sql")
class PostServiceTest(
    @Autowired private val postService: PostService,
    @Autowired private val rxtx: TransactionalOperator,
    @Autowired private val client: DatabaseClient,
): StringSpec({

    beforeSpec {
        println(">> initialize db")
        val script = ClassPathResource("db-init/test.sql")
        client.inConnection { connection ->
            ScriptUtils.executeSqlScript(connection, script )
        }.block()
    }

    suspend fun getPostSize() = postService.getAll().toList().size

    "get all" {
        assertEquals(3, postService.getAll().toList().size)
        assertEquals(1, postService.getAll("2").toList().size)
    }

    "get" {
        postService.get(1).let {
            it.title shouldBe  "title 1"
            it.body shouldBe "blabla 01"
            it.authorId shouldBe 1234
        }
        shouldThrow<NotFoundException> {
            postService.get(-1)
        }
    }

    "create" {
        val request = SavePost("title 4", "blabla 04", 1234)
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            postService.create(request).let {
                it.title shouldBe request.title
                it.body shouldBe request.body
                it.authorId shouldBe request.authorId
            }
        }
    }

    "create fail and rollback" {
        val prevSize = getPostSize()
        shouldThrow<Exception> {
            postService.create(SavePost("error", "blabla 04", 1234))
        }
        getPostSize() shouldBe prevSize
    }

    "update" {
        val newAuthorId = 999_999L
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            postService.update(1, SavePost(authorId=newAuthorId)).let {
                it.authorId = newAuthorId
            }
        }

    }

    "delete" {
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            val prevSize = getPostSize()
            val created = postService.create(SavePost("title 4", "blabla 04", 1234))
            getPostSize() shouldBe prevSize + 1
            postService.delete(created.id)
            getPostSize() shouldBe prevSize
        }
    }

})