package dev.fastcampus.async.c5.threadcost

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val latcher = CountDownLatch(10_000)
    val sum = AtomicLong()
    measureTimeMillis {
        for (i in 1..latcher.count) {
            thread(name = "t-$i") {
                for (k in 1..100_000) {
                    sum.addAndGet(1L)
//                    println("${Thread.currentThread().name} : $sum")
                }
                latcher.countDown()
            }
        }
        latcher.await()
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}