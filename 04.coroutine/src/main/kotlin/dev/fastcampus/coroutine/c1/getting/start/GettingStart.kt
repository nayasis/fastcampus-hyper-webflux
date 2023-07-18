package dev.fastcampus.coroutine.c1.getting.start

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun workHard() {
    logger.debug { "start hard work" }
    delay(1.seconds)
    logger.debug { "work done" }
}

//fun main() {
//    runBlocking {
//        hardWork()
//    }
//}

suspend fun main() {
    workHard()
}