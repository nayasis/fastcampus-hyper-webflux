package dev.fastcampus.async.c2.sync

import mu.KotlinLogging
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

fun main() {

    logger.debug { "start" }

    val rs = fetchSlow()

    while(true) {
        if(rs.isDone) {
            logger.debug { "result : ${rs.get()}" }
            break
        } else {
            logger.debug { "waiting" }
        }
        sleep(500)
    }

}

private fun fetchSlow(): CompletableFuture<String> {
    return CompletableFuture.supplyAsync {
        sleep(3000)
        "Data from network"
    }
}