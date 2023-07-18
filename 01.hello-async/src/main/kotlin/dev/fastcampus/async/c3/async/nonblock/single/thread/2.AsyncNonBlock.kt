package dev.fastcampus.async.c3.async.nonblock.single.thread

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val singleThread = Dispatchers.Default.limitedParallelism(1)
    coroutineScope {
        launch(singleThread) { workHard() }
        launch(singleThread) { drinkCoffee() }
    }
}

private suspend fun workHard() {
    logger.debug { "start hard work" }
    while (true) {
        delay(1000)
    }
    logger.debug { "end hard work" }
}

private suspend fun drinkCoffee() {
    delay(1000)
    logger.debug { "make coffee" }
    delay(1000)
    logger.debug { "drink coffee" }
}