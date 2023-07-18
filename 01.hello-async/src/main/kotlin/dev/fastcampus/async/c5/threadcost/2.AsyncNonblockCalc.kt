package dev.fastcampus.async.c5.threadcost

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

fun main() {
    val sum = AtomicLong()
    measureTimeMillis {
        runBlocking {
            for (i in 1..1000) {
                launch {
                    for (k in 0..100_000) {
                        sum.addAndGet(1)
//                        println("${Thread.currentThread().name} : $sum")
                    }
                }
            }
        }
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}

//fun main() {
//    var sum = 0L
//    measureTimeMillis {
//        runBlocking {
//            for (i in 1..1000) {
//                launch {
//                    for (k in 0..100_000) {
//                        sum++
//                    }
//                }
//            }
//        }
//    }.let { println(">> sum: $sum, elapsed: $it ms") }
//}