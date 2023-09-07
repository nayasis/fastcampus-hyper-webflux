package dev.fastcampus.coroutine.c3.compare.coffee

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

//private val worker = Dispatchers.Default.limitedParallelism(1)
private val worker = newSingleThreadContext("employee")

fun main() {
    measureTimeMillis {
        runBlocking {
            repeat(2) {
                launch(worker) {
                    makeCoffee()
                }
            }
        }
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private suspend fun makeCoffee() {
    coroutineScope {
        launch {
            grindCoffee()
            brewCoffee()
        }
        launch {
            boilMilk()
            foamMilk()
        }
    }
    mixCoffeeAndMilk()
}

private suspend fun grindCoffee() {
    logger.debug { "커피 갈기" }
    delay(1000)
    logger.debug { "> 커피 가루" }
}

private suspend fun brewCoffee() {
    logger.debug { "커피 내리기" }
    delay(1000)
    logger.debug { "> 커피 원액" }
}

private suspend fun boilMilk() {
    logger.debug { "우유 끓이기" }
    delay(1000)
    logger.debug { "> 데운 우유" }
}

private suspend fun foamMilk() {
    logger.debug { "우유 거품내기" }
    delay(1000)
    logger.debug { "> 거품 우유" }
}

private suspend fun mixCoffeeAndMilk() {
    logger.debug { "커피와 우유 섞기" }
    delay(1000)
    logger.debug { "> 카페라떼" }
}