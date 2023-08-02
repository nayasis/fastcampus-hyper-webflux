package dev.fastcampus.coroutine.c7.awaitAll

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun downloadA() {
    repeat(1) {
        logger.debug { "download A" }
        delay(1.seconds)
    }
}

suspend fun downloadB() {
    repeat(3) {
        logger.debug { "download B" }
        delay(1.seconds)
    }
}

suspend fun downloadC() {
    repeat(5) {
        logger.debug { "download C" }
        delay(1.seconds)
    }
}

suspend fun main() {
    coroutineScope {
//        launch { downloadA() }
//        launch { downloadB() }
//        launch { downloadC() }
        listOf(
            launch { downloadA() },
            launch { downloadB() },
            launch { downloadC() },
        ).joinAll()
        logger.debug { ">> done" }
    }
}