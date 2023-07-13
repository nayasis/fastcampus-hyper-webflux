package dev.fastcampus.coroutine.c1.performance

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        runBlocking {
            for(i in 1..10_000) {
                launch {
                    for(i in i..1000) {
                        println("[${Thread.currentThread().name}] $i")
                    }
                }
            }
        }
    }.let { logger.debug(">> done (${it}ms)") }
}