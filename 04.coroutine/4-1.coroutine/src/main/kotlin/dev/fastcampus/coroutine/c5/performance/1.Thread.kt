package dev.fastcampus.coroutine.c5.performance

import mu.KotlinLogging
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    val latcher = CountDownLatch(10_000)
    var sum = AtomicLong()
    measureTimeMillis {
        repeat(latcher.count.toInt()) { i ->
            thread(name="t-$i") {
                repeat(1000) {
                    sum.addAndGet(1)
//                    println("[${Thread.currentThread().name}] $i")
                }
                latcher.countDown()
            }
        }
        latcher.await()
    }.let { logger.debug(">> sum: $sum, done $it ms") }
}