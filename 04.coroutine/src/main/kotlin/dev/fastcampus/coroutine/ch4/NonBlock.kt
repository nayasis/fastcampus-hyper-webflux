package dev.fastcampus.coroutine.ch4

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val dispatcher = Dispatchers.Default.limitedParallelism(1)
    coroutineScope {
        launch(dispatcher) {
            heavy()
        }
        launch(dispatcher) {
            soft()
        }
    }
}

private suspend fun soft() {
    logger.debug { "start" }
    delay(1000)
    logger.debug { "end" }
}

private suspend fun heavy() {
    logger.debug { "start" }
    while (true) {
        delay(100)
    }
    logger.debug { "end" }
}

