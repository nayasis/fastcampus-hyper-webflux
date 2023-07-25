package dev.fastcampus.async.c2.coffee

import mu.KotlinLogging
import java.time.temporal.ChronoUnit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}


private val employees = Executors.newFixedThreadPool(3)

fun main() {

    measureTimeMillis {

        repeat(5) {
            makeCoffee()
        }

        employees.shutdown()
        employees.awaitTermination(100, TimeUnit.SECONDS)

    }.let { logger.debug { ">> elapsed : $it ms" } }

}

private fun makeCoffee() {
    val taskA = employees.submit {
        grindCoffee()
        brewCoffee()
    }
    val taskB = employees.submit {
        boilMilk()
        foamMilk()
    }
    employees.submit {
        while(! taskA.isDone || ! taskB.isDone ) {
            Thread.sleep(10)
        }
        mixCoffeeAndMilk()
    }
}

private fun grindCoffee() {
    logger.debug { "커피 갈기" }
    Thread.sleep(1000)
    logger.debug { "> 커피 가루" }
}

private fun brewCoffee() {
    logger.debug { "커피 내리기" }
    Thread.sleep(1000)
    logger.debug { "> 커피 원액" }
}

private fun boilMilk() {
    logger.debug { "우유 끓이기" }
    Thread.sleep(1000)
    logger.debug { "> 데운 우유" }
}

private fun foamMilk() {
    logger.debug { "우유 거품내기" }
    Thread.sleep(1000)
    logger.debug { "> 거품 우유" }
}

private fun mixCoffeeAndMilk() {
    logger.debug { "커피와 우유 섞기" }
    Thread.sleep(1000)
    logger.debug { "> 카페라떼" }
}