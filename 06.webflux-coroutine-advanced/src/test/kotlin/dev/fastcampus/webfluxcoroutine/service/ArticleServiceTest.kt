package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionCallback
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator,
) : StringSpec({

//    beforeTest {
//        repository.deleteAll()
//    }

    "get all" {
        rxtx.rollback {
            service.create(ReqCreate("title 1"))
            service.create(ReqCreate("title 2"))
            service.create(ReqCreate("title matched"))
            service.getAll().toList().size shouldBe 3
            service.getAll("matched").toList().size shouldBe 1
        }
    }

    "create and get" {
        rxtx.rollback {
            val created = service.create(ReqCreate("title1"))
            val get = service.get(created.id)
            get.id shouldBe created.id
            get.title shouldBe created.title
            get.authorId shouldBe created.authorId
            get.createdAt shouldNotBe null
            get.updatedAt shouldNotBe null
        }
    }

    "update" {
        rxtx.rollback {
            val created = service.create(ReqCreate("title 1"))
            service.update(created.id, ReqUpdate("body", "updated body"))
            val updated = service.get(created.id)
            updated.body shouldBe "updated body"
        }
    }
    "delete" {
        rxtx.rollback {
            val prevCnt = repository.count()
            val created = service.create(ReqCreate("title 1"))
            repository.count() shouldBe prevCnt + 1
            service.delete(created.id)
            repository.count() shouldBe prevCnt
        }
    }
})

suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T): T {
    return this.executeAndAwait { tx ->
        tx.setRollbackOnly()
        f.invoke(tx)
    }
}