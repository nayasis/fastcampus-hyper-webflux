package dev.fastcampus.reactor.`04`.subscribe

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

private val logger = KotlinLogging.logger {}

fun main() {

    val customScheduler = Schedulers.newParallel("custom", 2)

    Flux.range(1,100)
        .doOnNext { logger.debug { "1st : $it" } }
        .filter { it % 2 == 0 }
        .doOnNext { logger.debug { "2nd : $it" } }
        .map { it * 3 }
        .doOnNext { logger.debug { "3rd : $it" } }
//        .publishOn(Schedulers.boundedElastic())
        .publishOn(customScheduler)
        .filter { it % 8 == 0 }
//        .publishOn(Schedulers.boundedElastic())
//        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext { logger.debug { "4th : $it" } }
        .collectList()
        .doOnNext { logger.debug { "5th : $it" } }
//        .subscribe()
        .block()

}