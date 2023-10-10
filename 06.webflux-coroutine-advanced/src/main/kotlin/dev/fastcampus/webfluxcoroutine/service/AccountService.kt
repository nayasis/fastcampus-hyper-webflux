package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.exception.NoArticleFound as NoAccountFound
import dev.fastcampus.webfluxcoroutine.model.Article
import kotlinx.coroutines.delay
import mu.KotlinLogging
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository as AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class AccountService(
    private val repository: AccountRepository
) {

    suspend fun get(id: Long): Account {
        return repository.findById(id)?.toAccount()
            ?: throw NoAccountFound("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long): Account {
        logger.debug { "1. request" }
//        return repository.findById(id)?.apply {
        return repository.findArticleById(id)?.apply {
            logger.debug { "2. get data from db" }
            delay(3000)
            balance += amount
            repository.save(this).also {
                logger.debug { "3. update balance" }
            }
        }?.toAccount() ?: throw NoAccountFound("id: $id")
    }

}

data class Account(
    val id: Long,
    val balance: Long,
)

fun Article.toAccount(): Account {
    return Account(
        id = this.id,
        balance = this.balance,
    )
}