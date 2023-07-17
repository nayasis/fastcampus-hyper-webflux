package dev.fastcampus.coroutine.c3.async.nonblock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() {
    logger.debug { "start" }
    val dispatcher = Dispatchers.Default.limitedParallelism(1)
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

private suspend fun subA() {
    logger.debug { "start" }
    subB()
    logger.debug { "end" }
}

private suspend fun subB() {
    logger.debug { "start" }
    Thread.sleep(5000)
    logger.debug { "end" }
}