package dev.fastcampus.async.c2.sync.async.block.nonblock

import mu.KotlinLogging
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

fun main() {
    val rsA = subA()
    val rsB = subB()
    val rsC = subC()
    while (true) {
        if(rsA.isDone && rsB.isDone && rsC.isDone) {
            logger.debug { "all works done" }
            break
        } else {
            logger.debug { "waiting" }
        }
        Thread.sleep(500)
    }
}

private fun subA(): CompletableFuture<Unit> {
    logger.debug { "start" }
    try {
        return workHard()
    } finally {
        logger.debug { "end" }
    }
}

private fun subB(): CompletableFuture<Unit> {
    logger.debug { "start" }
    try {
        return workHard()
    } finally {
        logger.debug { "end" }
    }
}

private fun subC(): CompletableFuture<Unit> {
    logger.debug { "start" }
    try {
        return workHard()
    } finally {
        logger.debug { "end" }
    }
}

private fun workHard(): CompletableFuture<Unit> {
    return CompletableFuture.supplyAsync {
        Thread.sleep(3000)
    }
}