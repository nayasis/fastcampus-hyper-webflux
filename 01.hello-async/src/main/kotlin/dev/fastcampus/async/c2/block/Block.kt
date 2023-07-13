package dev.fastcampus.async.c2.block

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
    Thread.sleep(5000)
    logger.debug { "end" }
}