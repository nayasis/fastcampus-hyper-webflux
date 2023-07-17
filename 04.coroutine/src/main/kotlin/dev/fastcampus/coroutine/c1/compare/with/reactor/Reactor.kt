package dev.fastcampus.coroutine.c1.compare.with.reactor

import mu.KotlinLogging
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
//        runSimultaneously()
        runSequentially()
    }.let { logger.debug{">> elapsed : $it ms"} }
}

private fun runSequentially() {
    subA().then(subB()).then(subC()).block()
}

private fun runSimultaneously() {
    val latcher = CountDownLatch(3)
    subA().subscribe{ latcher.countDown() }
    subB().subscribe{ latcher.countDown() }
    subC().subscribe{ latcher.countDown() }
    latcher.await()
}

private fun subA(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext {
            logger.debug {"end"}
        }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext {
            logger.debug {"end"}
        }
}

private fun subC(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext {
            logger.debug {"end"}
        }
}