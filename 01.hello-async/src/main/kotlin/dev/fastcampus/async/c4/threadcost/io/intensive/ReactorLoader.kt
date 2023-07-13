package dev.fastcampus.async.c4.threadcost.io.intensive

import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis

fun main() {
    measureTimeMillis {
        val latcher = CountDownLatch(200)
        for(i in 1..latcher.count) {
            Mono.delay(Duration.ofMillis(100))
                .doOnNext {
//                    println("done $i : ${Thread.currentThread().name}")
                    latcher.countDown()
                }.subscribe()
        }
        latcher.await()
    }.let { println(">> elapsed: $it ms") }
}