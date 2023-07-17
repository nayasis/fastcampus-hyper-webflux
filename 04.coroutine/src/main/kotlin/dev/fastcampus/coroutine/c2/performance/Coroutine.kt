package dev.fastcampus.coroutine.c2.performance

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        runBlocking {
            var sum = 0
            for(i in 1..10_000) {
                launch {
                    for(k in 1..1000) {
                        sum++
                    }
                    println("[${Thread.currentThread().name}] $i : $sum")
                }

            }
        }
    }.let { logger.debug(">> done (${it}ms)") }
}
