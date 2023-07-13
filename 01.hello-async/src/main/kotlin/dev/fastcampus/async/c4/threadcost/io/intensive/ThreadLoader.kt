package dev.fastcampus.async.c4.threadcost.io.intensive

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val latcher = CountDownLatch(200)
    measureTimeMillis {
        for (i in 1..latcher.count) {
            thread(name = "t-$i") {
                Thread.sleep(100)
//                println("done $i : ${Thread.currentThread().name}")
                latcher.countDown()
            }
        }
        latcher.await()
    }.let { println(">> elapsed: $it ms") }
}