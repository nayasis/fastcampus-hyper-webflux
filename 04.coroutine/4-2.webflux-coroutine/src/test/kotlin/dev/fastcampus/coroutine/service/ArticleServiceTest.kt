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
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
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

    suspend fun getArticleSize() = articleService.getAll().toList().size

    "get all" {
        assertEquals(3, articleService.getAll().toList().size)
        assertEquals(1, articleService.getAll("2").toList().size)
    }

    "get" {
        articleService.get(1).let {
            it.title shouldBe  "title 1"
            it.body shouldBe "blabla 01"
            it.authorId shouldBe 1234
        }
        shouldThrow<NotFoundException> {
            articleService.get(-1)
        }
    }

    "create" {
        val request = SaveArticle("title 4", "blabla 04", 1234)
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            articleService.create(request).let {
                it.title shouldBe request.title
                it.body shouldBe request.body
                it.authorId shouldBe request.authorId
            }
        }
    }

    "create fail and rollback" {
        val prevSize = getArticleSize()
        shouldThrow<Exception> {
            articleService.create(SaveArticle("error", "blabla 04", 1234))
        }
        getArticleSize() shouldBe prevSize
    }

    "update" {
        val newAuthorId = 999_999L
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            articleService.update(1, SaveArticle(authorId=newAuthorId)).let {
                it.authorId = newAuthorId
            }
        }

    }

    "delete" {
        rxtx.executeAndAwait { tx ->
            tx.setRollbackOnly()
            val prevSize = getArticleSize()
            val created = articleService.create(SaveArticle("title 4", "blabla 04", 1234))
            getArticleSize() shouldBe prevSize + 1
            articleService.delete(created.id)
            getArticleSize() shouldBe prevSize
        }
    }

})