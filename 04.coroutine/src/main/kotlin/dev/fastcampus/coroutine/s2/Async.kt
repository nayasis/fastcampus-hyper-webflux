package dev.fastcampus.coroutine.s2

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {

        val job1 = async {
            var cnt = 0
            repeat(10) {
                cnt++
                delay(1000)
                println("job1 : $cnt")
            }

            cnt
        }
        val job2 = async {
            var cnt = 0
            repeat(5) {
                cnt++
                delay(2000)
                println("job2 : $cnt")
            }
            cnt
        }

        val sum = job1.await() + job2.await()

        println(sum)

    }
}