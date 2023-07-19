package dev.fastcampus.coroutine.cA.cancel

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import mu.KotlinLogging
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun queryDb() {
    logger.debug { "start query" }
    try {
        repeat(1000) { i ->
            println("fetching... $i")
            delay(500.milliseconds)
        }
    } finally {
        withContext(NonCancellable) {
            logger.debug { "I'm running finally" }
            delay(1.seconds)
            logger.debug { "delayed in final" }
        }
    }
    logger.debug { "end query" }
}

suspend fun queryHeavy() {
    logger.debug { "start heavy query" }
    val start = System.currentTimeMillis()
    var nextPrintTime = start
    try {
        while (true) {
            if(System.currentTimeMillis() >= nextPrintTime) {
                logger.debug { "fetching..." }
                nextPrintTime += 500
                yield()
            }
        }
    } catch (e: Exception) {
        logger.debug( "stop")
    }
}

class Connection: AutoCloseable {
    init {
        logger.debug { "open connection" }
    }
    override fun close() {
        logger.debug { "close connection" }
    }
}

suspend fun wait5secWhileQueryDb() {
    coroutineScope {
        val job = launch {
            Connection().use { queryHeavy() }
            Connection().use { queryDb() }
        }
        job.invokeOnCompletion {e ->
            logger.debug { "job completed" }
        }
        delay(5.seconds)
//        job.cancel()
        job.cancelAndJoin()
        logger.debug { "stop query" }
    }
}

suspend fun main() {
    withTimeout(1.seconds) {
        wait5secWhileQueryDb()
    }
}