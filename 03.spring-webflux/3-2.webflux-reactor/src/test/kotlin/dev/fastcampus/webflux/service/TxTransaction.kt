package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.service.TxTransaction.Companion.rxtx
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


// Spring test 의 @Transactional 은 threadlocal 기반이라 webflux 에서 작동하지 않는다.
// https://github.com/spring-projects/spring-framework/issues/24226

@Component
class TxTransaction: ApplicationContextAware {

    override fun setApplicationContext(context: ApplicationContext) {
        rxtx = context.getBean(TransactionalOperator::class.java)
    }

    companion object {
        lateinit var rxtx: TransactionalOperator
            private set
    }



}

fun <T> Mono<T>.rollback(): Mono<T> {
    val publisher = this
    return rxtx.execute { tx ->
        tx.setRollbackOnly()
        publisher
    }.next()
}

fun <T> Flux<T>.rollback(): Flux<T> {
    val publisher = this
    return rxtx.execute { tx ->
        tx.setRollbackOnly()
        publisher
    }
}

fun String.merong() {

}
