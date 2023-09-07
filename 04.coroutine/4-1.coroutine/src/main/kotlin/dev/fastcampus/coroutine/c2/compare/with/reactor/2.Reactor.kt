package dev.fastcampus.coroutine.c2.compare.with.reactor

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
    Mono.zip(
        subA(),
        subB(),
        subC(),
    ).block()
}

private fun subA(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext { logger.debug {"end"} }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext { logger.debug {"end"} }
}

private fun subC(): Mono<Unit> {
    return Mono.fromCallable{ logger.debug {"start"} }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext { logger.debug {"end"} }
}