package dev.fastcampus.reactor.`02`.function.call

import reactor.core.publisher.Mono

fun getRequest(): Int {
    return 1
}

fun subA(i: Int): Mono<Int> {
    return Mono.fromCallable { i + 1 }
}

fun subB(i: Int): Mono<Int> {
    return Mono.fromCallable { i + 2 }
}

fun main() {
    val request = getRequest()
    subA(request)
//        .doOnNext { println("ans1 : $it") }
        .flatMap { subB(it) }
//        .doOnNext { println("ans2 : $it") }
        .subscribe()
}