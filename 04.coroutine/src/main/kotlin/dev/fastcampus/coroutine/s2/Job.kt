package dev.fastcampus.coroutine.s2

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("count $i")
                    delay(500L)
                }
            } catch (e: Exception) {
                println(">> stop")
            }
        }

        delay(1000L)
        println(">> done")

        job.cancelAndJoin()
        println(">> canceled")
    }
}