package dev.fastcampus.coroutine.c2.performance

import mu.KotlinLogging
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    val latcher = CountDownLatch(10_000)
    measureTimeMillis {
        for(i in 1..latcher.count) {
            thread(name="t-$i") {
                for(i in 1..1000) {
                    println("[${Thread.currentThread().name}] $i")
                }
                latcher.countDown()
            }
        }
        latcher.await()
    }.let { logger.debug { ">> done (${it}ms)" } }
}