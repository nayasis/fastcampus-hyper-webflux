package dev.fastcampus.async.c3.reactor

import mu.KotlinLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

private val scheduler = Schedulers.newSingle("worker")
@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        Mono.fromCallable { subA(1) }.subscribeOn(scheduler).subscribe()
        Mono.fromCallable { subA(2) }.subscribeOn(scheduler).subscribe()
        Mono.fromCallable { subA(3) }.subscribeOn(scheduler).block()
    }.let { logger.debug { "${it.inWholeSeconds} sec" } }
    scheduler.dispose()
}

private fun subA(i: Int) {
    logger.debug { "start-$i" }
    subB(i)
    logger.debug { "end-$i" }
}

private fun subB(i: Int) {
    logger.debug { "start-$i" }
    Thread.sleep(5000)
    logger.debug { "end-$i" }
}