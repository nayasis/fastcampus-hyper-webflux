package dev.fastcampus.coroutine.service

import dev.fastcampus.coroutine.repository.PostRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineDispatcher
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@Service
class TestService(
    private val repository: PostRepository,
) {

    suspend fun testMdcLogging(): String? {

        logger.debug( "mdc test 1, txid : ${getContext()?.get<String>("txid")}" )

        val res = mono(Schedulers.boundedElastic().asCoroutineDispatcher()) {
            logger.debug { "mdc test 2 : ${MDC.getCopyOfContextMap()}" }
            "1"
        }.awaitSingle()

//        val res = Mono.just("one")
//            .publishOn(Schedulers.boundedElastic())
//            .flatMap {
//                logger.debug { "mdc test 2 : ${MDC.getCopyOfContextMap()}" }
//                Mono.just("1")
//            }.awaitSingle()

        logger.debug { "before delay" }

        delay(500.milliseconds)

        logger.debug { "after delay" }

        val list = repository.findAll().toList()

        logger.debug { "mdc test 3 : ${res} + ${list.size}" }

        logger.debug( "mdc test 4, txid : ${getContext()?.get<String>("txid")}" )

        logger.debugObserved { "mdc test 5" }

        within {
            logger.debug { "mdc test 6" }
        }

        logger.debug { "mdc test 7" }

        throw RuntimeException("error test")

//            return res

    }

}

suspend fun getContext(): Context? {
    return currentCoroutineContext()[ReactorContext]?.context
}

suspend fun KLogger.debugObserved(msg: () -> Any?) {
    within { debug(msg) }
}

suspend inline fun <T: Any?> within(crossinline f: () -> T): T {
    return Mono.fromCallable { f() }.awaitSingle()
}