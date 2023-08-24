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

val simpleDispatcher = Dispatchers.Default
val blockingDispatcher = Dispatchers.IO
val customDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
@OptIn(DelicateCoroutinesApi::class)
val anotherCustomDispatcher = newFixedThreadPoolContext(4, "another")

suspend fun main() {
    logger.debug { "start" }
    val dispatcher = newSingleThreadContext("single")
    coroutineScope {
        val t1 = launch(dispatcher) {
            subA()
        }
        val t2 = launch(dispatcher) {
            subA()
        }
        delay(5000)
        t1.cancel()
        t2.cancel()
    }
    logger.debug { "end" }
}

private suspend fun subA() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private suspend fun workHard() {
    logger.debug { "start" }
    while (true) {
        delay(100)
    }
    logger.debug { "end" }
}