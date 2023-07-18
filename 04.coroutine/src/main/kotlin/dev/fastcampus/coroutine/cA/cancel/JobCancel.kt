package dev.fastcampus.coroutine.cA.cancel

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun queryDb() {
    logger.debug { "start query" }
    delay(10.seconds)
    logger.debug { "end query" }
}

suspend fun wait2secWhileQueryDb() {
    coroutineScope {
        val job = launch { queryDb() }
        delay(2.seconds)
        job.cancel()
        logger.debug { "stop query" }
    }
}

suspend fun main() {
    wait2secWhileQueryDb()
}