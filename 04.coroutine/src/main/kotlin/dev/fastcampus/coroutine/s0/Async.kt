package dev.fastcampus.coroutine.s0

import mu.KotlinLogging
import java.lang.Thread.sleep
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

fun main() {
    logger.debug("start main")
    thread {
        sub()
    }
    logger.debug("end main")
}

private fun sub() {
    logger.debug("start sub")
    sleep(5000)
    logger.debug("end sub")
}