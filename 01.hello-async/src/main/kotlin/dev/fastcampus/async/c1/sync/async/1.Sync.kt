package dev.fastcampus.async.c1.sync.async

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    subA()
    subB()
    subC()
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