package dev.fastcampus.async.c3.async.reactor

import mu.KotlinLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

private val scheduler = Schedulers.newSingle("worker")

fun main() {
    measureTimeMillis {
        main(1).subscribeOn(scheduler).subscribe()
        main(2).subscribeOn(scheduler).subscribe()
        main(3).subscribeOn(scheduler).block()
    }.let { logger.debug { "$it ms" } }
    scheduler.dispose()
}

private fun main(i: Int): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start-$i: mainA" } }
        .then(subA(i))
        .doOnNext {  logger.debug { "end-$i: mainA" } }
}

private fun subA(i: Int): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start-$i: subA" } }
        .then(subB(i))
        .doOnNext {  logger.debug { "end-$i: subA" } }


}

private fun subB(i: Int): Mono<Unit> {
    return Mono.fromCallable{ logger.debug { "start-$i: subB" } }
        .doOnNext { Thread.sleep(3000) }
        .publishOn(scheduler)
        .doOnNext { logger.debug { "end-$i: subB" } }
}