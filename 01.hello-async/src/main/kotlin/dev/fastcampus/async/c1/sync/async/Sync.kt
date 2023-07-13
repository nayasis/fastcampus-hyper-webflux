package dev.fastcampus.async.c1.sync.async

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    logger.debug { "start" }
    subA()
    logger.debug { "end" }
}

private fun subA() {
    logger.debug { "start" }
    subB()
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    logger.debug { "end" }
}