package dev.fastcampus.async.c1.sample

import mu.KotlinLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

val singleThread = Schedulers.newSingle("worker")

fun main() {
    measureTimeMillis {
        Mono.zip(
            subA(),
            subB()
        ).subscribeOn(singleThread).block()
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private fun subA(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start sub A" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(singleThread)
        .doOnNext { logger.debug { "end sub A" } }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start sub B" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(singleThread)
        .doOnNext { logger.debug { "end sub B" } }
}


