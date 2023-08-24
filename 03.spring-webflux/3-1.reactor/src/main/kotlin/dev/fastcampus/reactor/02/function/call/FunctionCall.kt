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

//    val request = subA()
//
//    println("request : ${request.block()}")
//
//    val ans1 = subB(request)
//
//    println("ans1 : ${ans1.block()}")
//
//    val ans2 = subC(ans1)
//
////    println("result : $ans2")
//
//    println("ans2 : ${ans2.block()}")
//
//    ans2.subscribe{
//        println("final : $it")
//    }

    subC(
        subB(
            subA().doOnNext{
                println("request : $it")
            }
        ).doOnNext {
            println("ans1 : $it")
        }
    ).doOnNext {
        println("ans2 : $it")
    }.subscribe()

//    subA().doOnNext { println("request: $it") }
//        .flatMap { subB(Mono.just(it)) }.doOnNext { println("ans1: $it") }
//        .flatMap { subC(Mono.just(it)) }.doOnNext { println("ans2: $it") }
//        .subscribe()

}