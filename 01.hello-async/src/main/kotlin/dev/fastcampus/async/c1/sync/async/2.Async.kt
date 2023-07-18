package dev.fastcampus.async.c1.sync.async

import mu.KotlinLogging
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

fun main() {
    thread { subA() }
    thread { subB() }
    thread { subC() }
}


private fun subA() {
    logger.debug { "start" }
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    logger.debug { "end" }
}

private fun subC() {
    logger.debug { "start" }
    logger.debug { "end" }
}