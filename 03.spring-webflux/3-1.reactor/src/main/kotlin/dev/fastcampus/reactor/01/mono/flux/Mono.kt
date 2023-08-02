package dev.fastcampus.reactor.`01`.mono.flux

import reactor.core.publisher.Mono

fun add(i : Int): Int {
    return i + 5
}

fun addAsync(i: Int): Mono<Int> {
    return Mono.fromCallable { i + 5 }
}

fun main() {

//    Mono.just(1).doOnNext { logger.debug("delivered :$it") }.subscribe()

//    Flux.just(1,2,3).doOnNext { logger.debug { "delivered: $it" } }.subscribe()

//    Flux.just(1..10).doOnNext { println(it) }.subscribe()
//    Flux.range(1,10).doOnNext { println(it) }.subscribe()
//    Flux.range(1,10).map { it + 5 }.doOnNext { println(it) }.subscribe()
//    Flux.range(1,10).flatMap { Mono.just(it + 5) }.doOnNext { println(it) }.subscribe()
//    Flux.range(1,10).map { add(it) }.doOnNext { println(it) }.subscribe()
//    Flux.range(1,10).flatMap {  addAsync(it) }.doOnNext { println(it) }.subscribe()


//    Mono.just(1).flux().doOnNext { println(it) }.subscribe()

//    println( final.subscribe() )


}