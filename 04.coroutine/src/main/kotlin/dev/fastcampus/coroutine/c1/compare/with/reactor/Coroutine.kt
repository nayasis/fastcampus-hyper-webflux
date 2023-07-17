package dev.fastcampus.coroutine.c1.compare.with.reactor

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun main() {
    measureTimeMillis {
        runSimultaneously()
//        runSequentially()
    }.let { logger.debug{">> elapsed : $it ms"} }
}

private suspend fun runSequentially() {
    subA()
    subB()
    subC()
}

private suspend fun runSimultaneously() {
    coroutineScope {
        launch { subA() }
        launch { subB() }
        launch { subC() }
    }
}

private suspend fun subA() {
    logger.debug {"start"}
    delay(1.seconds)
    logger.debug {"end"}
}

private suspend fun subB() {
    logger.debug {"start"}
    delay(1.seconds)
    logger.debug {"end"}
}

private suspend fun subC() {
    logger.debug {"start"}
    delay(1.seconds)
    logger.debug {"end"}
}