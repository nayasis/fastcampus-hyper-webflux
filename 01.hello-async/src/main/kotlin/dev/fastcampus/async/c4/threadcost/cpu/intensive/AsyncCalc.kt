package dev.fastcampus.async.c4.threadcost.cpu.intensive

import reactor.core.publisher.Mono
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val latcher = CountDownLatch(1000)
    val sum = AtomicLong()
    measureTimeMillis {
        for (i in 0..latcher.count) {
            Mono.fromCallable {
                for (k in 0..100_000) {
                    sum.accumulateAndGet(1L) { x, y -> x + y }
                }
                latcher.countDown()
            }.subscribe()
        }
        latcher.await()
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}