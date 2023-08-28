package dev.fastcampus.coroutine.c6.async.nonblock

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

val singleDispatcher = newSingleThreadContext("single")
val simpleDispatcher = Dispatchers.Default
val blockingDispatcher = Dispatchers.IO
val customDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
@OptIn(DelicateCoroutinesApi::class)
val anotherCustomDispatcher = newFixedThreadPoolContext(4, "another")

suspend fun main() {
    logger.debug { "start" }
    coroutineScope {
        val taskHard = launch(singleDispatcher) {
            workHard()
        }
        val taskEasy = launch(singleDispatcher) {
            workEasy()
        }
        taskEasy.join()
        delay(2000)
        taskHard.cancel()
    }
    logger.debug { "end" }
}

private suspend fun workEasy() {
    logger.debug { "start easy work" }
    delay(1000)
    logger.debug { "end easy work" }
}

private suspend fun workHard() {
    logger.debug { "start hard work" }
    try {
        while (true) {
            delay(100)
        }
    } finally {
        logger.debug { "end hard work" }
    }
}