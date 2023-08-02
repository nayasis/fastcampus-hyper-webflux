package dev.fastcampus.async.c1.sample

import mu.KotlinLogging
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        val taskA = subA()
        while(! taskA.isDone) {
            subB()
            Thread.sleep(2000)
        }
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private fun subA(): CompletableFuture<Unit> {
    return CompletableFuture.supplyAsync {
        logger.debug { "start sub A" }
        Thread.sleep(3000)
        logger.debug { "end sub A" }
    }
}

private fun subB() {
    logger.debug { "start sub B" }
    Thread.sleep(1000)
    logger.debug { "end sub B" }
}