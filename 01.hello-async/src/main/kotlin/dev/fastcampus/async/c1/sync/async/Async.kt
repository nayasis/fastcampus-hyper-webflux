package dev.fastcampus.async.c1.sync.async

import mu.KotlinLogging
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

fun main() {
    logger.debug { "start" }
    thread { subA() }
    logger.debug { "end" }
}

private fun subA() {
    logger.debug { "start" }
    thread { subB() }
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    logger.debug { "end" }
}