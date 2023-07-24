package dev.fastcampus.coroutine.c6.async.nonblock

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    logger.debug { "start" }
    val dispatcher = newSingleThreadContext("single")
    coroutineScope {
        launch(dispatcher) {
            subA()
        }
        launch(dispatcher) {
            subA()
        }
    }
    logger.debug { "end" }
}

private fun subA() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun workHard() {
    logger.debug { "start" }
    Thread.sleep(5000)
    logger.debug { "end" }
}