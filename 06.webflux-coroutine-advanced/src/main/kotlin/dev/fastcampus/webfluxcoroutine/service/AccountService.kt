package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.model.Article
import kotlinx.coroutines.delay
import mu.KotlinLogging
import dev.fastcampus.webfluxcoroutine.exception.NoArticleFound as NoAccountFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.TemporalAmount
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository as AccountRepository

private val logger = KotlinLogging.logger {}

@Service
class AccountService(
    private val repository: AccountRepository
) {
    suspend fun get(id: Long): ResAccount {
        return repository.findById(id)?.toResAccount() ?: throw NoAccountFound("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long) {
//        repository.findById(id)?.let { account ->
        logger.debug { "1. request" }
        repository.findArticleById(id)?.let { account ->
            logger.debug { "2. read data" }
            delay(3000)
            account.balance += amount
            repository.save(account)
            logger.debug { "3. update data" }
        } ?: throw NoAccountFound("id: $id")
    }
}

data class ResAccount(
    val id: Long,
    val balance: Long,
)

fun Article.toResAccount(): ResAccount {
    return ResAccount(
        id = this.id,
        balance = this.balance,
    )
}