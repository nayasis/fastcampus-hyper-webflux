package dev.fastcampus.async.c3.async.nonblock.single.thread

import mu.KotlinLogging
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

fun main() {
    // thread 2개가 필요
    thread { workHard() }
    thread { drinkCoffee() }
}

private fun workHard() {
    logger.debug { "start hard work" }
    while (true) {
        // do something
    }
    logger.debug { "end hard work" }
}

private fun drinkCoffee() {
    logger.debug { "make coffee" }
    Thread.sleep(1000)
    logger.debug { "drink coffee" }
}