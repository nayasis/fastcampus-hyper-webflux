package dev.fastcampus.async.c3.async.nonblock.single.thread

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

private val logger = KotlinLogging.logger {}

val single = Schedulers.newSingle("single")

fun main() {

    drinkCoffee().subscribe()
    workHard().blockLast()

}

private fun workHard(): Flux<*> {
    return Flux.interval(Duration.ofMillis(500))
        .publishOn(single)
        .doOnNext {
            logger.debug { "processing" }
        }.doFinally {
            logger.debug { "end hard work" }
        }.doFirst {
            logger.debug { "start hard work" }
        }.subscribeOn(single)
}

private fun drinkCoffee(): Mono<*> {
    return Mono.delay(Duration.ofSeconds(1))
        .publishOn(single)
        .doOnNext { logger.debug { "make coffee" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(single)
        .doOnNext {
            logger.debug { "drink coffee" }
        }
        .subscribeOn(single)
}