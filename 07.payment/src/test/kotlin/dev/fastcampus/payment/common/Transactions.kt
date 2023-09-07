package dev.fastcampus.payment.common

import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T): T {
    return executeAndAwait { tx ->
        tx.setRollbackOnly()
        f.invoke(tx)
    }
}