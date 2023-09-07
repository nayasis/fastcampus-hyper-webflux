package dev.fastcampus.async.c2.coffee

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

private val workers = Schedulers.newSingle("employee")

fun main() {
    measureTimeMillis {
        Flux.range(1,5).flatMap {
            makeCoffee()
        }.subscribeOn(workers).blockLast()
//        makeCoffee().block()
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private fun makeCoffee(): Mono<Unit> {
    return Mono.zip(
        grindCoffee().then(brewCoffee()),
        boilMilk().then(foamMilk()),
    ).then(mixCoffeeAndMilk())
}


private fun grindCoffee(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피 갈기" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(workers)
        .doOnNext { logger.debug { "> 커피 가루" } }
}

private fun brewCoffee(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피 내리기" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(workers)
        .doOnNext { logger.debug { "> 커피 원액" } }
}

private fun boilMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "우유 끓이기" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(workers)
        .doOnNext { logger.debug { "> 데운 우유" } }
}

private fun foamMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "우유 거품내기" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(workers)
        .doOnNext { logger.debug { "> 거품 우유" } }
}

private fun mixCoffeeAndMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피와 우유 섞기" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(workers)
        .doOnNext { logger.debug { "> 카페라떼" } }
}