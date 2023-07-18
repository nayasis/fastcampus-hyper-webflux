package dev.fastcampus.async.c2.sync.async.block.nonblock

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    subA()
    subB()
    subC()
}

private fun subA() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun subC() {
    logger.debug { "start" }
    workHard()
    logger.debug { "end" }
}

private fun workHard() {
    Thread.sleep(1000)
}