package dev.fastcampus.async.c1.sample

import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        subA()
        subB()
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

//// thread async block
//fun main() {
//    measureTimeMillis {
//        listOf(
//            thread { subA() },
//            thread { subB() },
//        ).forEach { it.join() }
//    }.let { logger.debug { ">> elapsed : $it ms" } }
//}

private fun subA() {
    logger.debug { "start sub A" }
    Thread.sleep(1000)
    logger.debug { "end sub A" }
}

private fun subB() {
    logger.debug { "start sub B" }
    Thread.sleep(1000)
    logger.debug { "end sub B" }
}


