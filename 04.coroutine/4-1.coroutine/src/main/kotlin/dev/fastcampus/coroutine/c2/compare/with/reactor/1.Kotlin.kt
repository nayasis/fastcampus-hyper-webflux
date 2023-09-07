package dev.fastcampus.coroutine.c2.compare.with.reactor

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    val sum = functionC()
    logger.debug { ">> sum : $sum" }
}


fun functionA(): Int {
    logger.debug { "tick" }
    Thread.sleep(1000)
    return 1
}

fun functionB(): Int {
    logger.debug { "tick" }
    Thread.sleep(1000)
    return 2
}

fun functionC(): Int {
    return functionA() + functionB()
}