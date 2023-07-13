package dev.fastcampus.async.c3.async.reactor

import mu.KotlinLogging
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

fun main() {
    subA().subscribe()
}

fun subA(): Mono<Unit> {
    logger.debug { "start" }
    try {
        return subB()
    } finally {
        logger.debug { "end" }
    }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable {
        logger.debug { "tick" }
    }
}