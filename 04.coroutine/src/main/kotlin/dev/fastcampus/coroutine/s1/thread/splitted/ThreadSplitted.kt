package dev.fastcampus.coroutine.s1.thread.splitted

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun run() {
    logger.debug { "my name is" }
    delay(1.seconds)
//    yield()
    logger.debug { "John doe." }
}

suspend fun main() {
    run()
}