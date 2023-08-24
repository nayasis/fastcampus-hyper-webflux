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
        // async 함수를 2번 불러도, 그 안에서 동작이 sync 라서, 결국 async 함수들은 synchronous 하게 움직인다.
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