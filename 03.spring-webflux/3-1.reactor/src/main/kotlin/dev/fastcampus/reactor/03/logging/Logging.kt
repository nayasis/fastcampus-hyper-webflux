package dev.fastcampus.reactor.`03`.logging

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

fun main() {

//    Mono.just(1)
//        .log()
//        .map { it * 2 }
//        .log()
//        .subscribe()

//    Flux.range(1, 10)
//        .log()
//        .map { it * 2 }
//        .log()
//        .subscribe()


//    Flux.range(1,100)
//        .log()
//        .filter { it % 2 == 0 }
//        .log()
//        .map { it * 3 }
//        .log()
//        .filter { it % 8 == 0 }
//        .collectList()
//        .log()
//        .subscribe()

//    Flux.range(1,10)
//        .doOnNext { logger.debug { "1st : $it" } }
//        .filter { it % 2 == 0 }
//        .doOnNext { logger.debug { "2nd : $it" } }
//        .map { it * 3 }
//        .doOnNext { logger.debug { "3rd : $it" } }
//        .filter { it % 8 == 0 }
//        .doOnNext { logger.debug { "4th : $it" } }
//        .collectList()
//        .doOnNext { logger.debug { "5th : $it" } }
//        .subscribe()

}