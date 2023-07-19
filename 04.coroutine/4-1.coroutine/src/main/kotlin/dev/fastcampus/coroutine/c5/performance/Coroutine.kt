package dev.fastcampus.coroutine.c5.performance

import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    var sum = AtomicLong()
    measureTimeMillis {
        runBlocking {
            repeat(10_000) { i ->
                launch {
                    repeat(1000) {
                        sum.addAndGet(1)
                    }
                    println("[${Thread.currentThread().name}] $i : $sum")
                }
            }
        }
    }.let { logger.debug(">> sum: $sum, done $it ms") }
}

//fun main() {
//    val lock = Mutex()
//    var sum = 0
//    measureTimeMillis {
//        runBlocking {
//            repeat(10_000) { i ->
//                launch {
//                    repeat(1000) {
//                        lock.withLock {
//                            sum++
//                        }
//                    }
////                    println("[${Thread.currentThread().name}] $i : $sum")
//                }
//            }
//        }
//    }.let { logger.debug(">> sum: $sum, done $it ms") }
//}


//fun main() {
//    val context = newFixedThreadPoolContext(4,"test")
//    val lock = Mutex()
//    var sum = 0
//    measureTimeMillis {
//        runBlocking {
//            repeat(10_000) { i ->
//                launch(context) {
//                    repeat(1000) {
//                        lock.withLock {
//                            sum++
//                        }
//                    }
////                    println("[${Thread.currentThread().name}] $i : $sum")
//                }
//            }
//        }
//    }.let { logger.debug(">> sum: $sum, done $it ms") }
//}

//fun main() {
//    val context = newFixedThreadPoolContext(4,"test")
//    var sum = AtomicLong()
//    measureTimeMillis {
//        runBlocking {
//            repeat(10_000) { i ->
//                launch(context) {
//                    repeat(1000) {
//                        sum.addAndGet(1)
//                    }
////                    println("[${Thread.currentThread().name}] $i : $sum")
//                }
//            }
//        }
//    }.let { logger.debug(">> sum: $sum, done $it ms") }
//}