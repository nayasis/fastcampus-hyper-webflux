package dev.fastcampus.reactor.`02`.function.call

import reactor.core.publisher.Mono

fun subA(): Mono<Int> {
    return Mono.just(1)
}

fun subB(mono: Mono<Int>): Mono<Int> {
    return mono.map { it + 1 }
}

fun subC(mono: Mono<Int>): Mono<Int> {
    return mono.map { it + 2 }
}

fun main() {

    val request = getRequest().doOnNext { logger.debug { ">> request: ${it}" } }

//    logger.debug { ">> request: ${request.block()}" }

    val resA = subA(request).doOnNext{ logger.debug { ">> resA: ${it}" } }

//    logger.debug { ">> resA: ${resA.block()}" }

    val resB = subB(resA).doOnNext{ logger.debug { ">> resB: ${it}" } }

//    logger.debug { ">> resB: ${resB.block()}" }

    resB.subscribe()

//    subA().doOnNext { println("request: $it") }
//        .flatMap { subB(Mono.just(it)) }.doOnNext { println("ans1: $it") }
//        .flatMap { subC(Mono.just(it)) }.doOnNext { println("ans2: $it") }
//        .subscribe()

}