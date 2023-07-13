package dev.fastcampus.async.c2.sync

import mu.KotlinLogging
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

fun main() {
    logger.debug { "start" }
    val rs = fetchSlow().get()
    logger.debug { "waiting" }
    logger.debug { "result : ${rs}" }
}

private fun fetchSlow(): CompletableFuture<String> {
    return CompletableFuture.supplyAsync {
        Thread.sleep(3000)
        "Data from network"
    }
}