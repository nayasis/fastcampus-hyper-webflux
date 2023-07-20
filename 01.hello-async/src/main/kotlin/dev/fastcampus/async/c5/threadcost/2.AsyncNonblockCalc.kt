package dev.fastcampus.async.c5.threadcost

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

fun main() {
    val latcher = CountDownLatch(10_000)
    val sum = AtomicLong()
    measureTimeMillis {
        Flux.range(1, latcher.count.toInt()).doOnNext {
            Flux.range(1, 100_000).doOnNext {
                sum.addAndGet(1)
            }.doFinally {
                latcher.countDown()
            }.subscribe()
        }.subscribe()
        latcher.await()
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}

//fun main() {
//    val sum = AtomicLong()
//    measureTimeMillis {
//        runBlocking {
//            repeat(10000) {
//                launch {
//                    repeat(100_000) {
//                        sum.addAndGet(1L)
//                    }
//                }
//            }
//        }
//    }.let { println(">> sum: $sum, elapsed: $it ms") }
//}