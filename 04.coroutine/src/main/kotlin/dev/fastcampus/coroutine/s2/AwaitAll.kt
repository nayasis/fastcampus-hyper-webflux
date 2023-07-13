package dev.fastcampus.coroutine.s2

import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {

    suspend fun downloadA() {
        repeat(10) {
            logger.debug("download A")
            delay(1000)
        }
    }

    suspend fun downloadB() {
        repeat(5) {
            logger.debug("download B")
            delay(1000)
        }
    }

    suspend fun downloadC() {
        repeat(2) {
            logger.debug("download C")
            delay(1000)
        }
    }

    runBlocking {
        val jobs = listOf(
            launch { downloadA() },
            launch { downloadB() },
            launch { downloadC() },
        )
        jobs.joinAll()
        logger.debug { ">> done" }
    }

}