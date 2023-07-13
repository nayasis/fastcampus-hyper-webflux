package dev.fastcampus.async.c2.block

import mu.KotlinLogging
import java.lang.Thread.sleep
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
    sleep(5000)
    logger.debug { "end" }
}