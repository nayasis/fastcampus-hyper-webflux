package dev.fastcampus.coroutine.c3.compare.coffee

import kotlinx.coroutines.async
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
                    makeCoffee().let {
                        logger.info { "[${it}]를 맛있게 먹는다." }
                    }
                }
            }
        }
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private suspend fun makeCoffee(): String {
    return coroutineScope {
        val task1 = async {
            grindCoffee().let { item -> brewCoffee(item) }
        }
        val task2 = async {
            boilMilk().let { item -> foamMilk(item) }
        }
        mixCoffeeAndMilk(listOf(task1.await(), task2.await()))
    }

}

private suspend fun grindCoffee(): String {
    logger.debug { "커피 갈기" }
    delay(1000)
    logger.debug { "> 커피 가루" }
    return "커피가루"
}

private suspend fun brewCoffee(item: String): String {
    logger.debug { "커피 내리기 from $item" }
    delay(1000)
    logger.debug { "> 커피 원액" }
    return "커피 원액"
}

private suspend fun boilMilk(): String {
    logger.debug { "우유 끓이기" }
    delay(1000)
    logger.debug { "> 데운 우유" }
    return "데운 우유"
}

private suspend fun foamMilk(item: String): String {
    logger.debug { "우유 거품내기" }
    delay(1000)
    logger.debug { "> 거품 우유" }
    return "거품우유"
}

private suspend fun mixCoffeeAndMilk(items: Collection<String>): String {
    logger.debug { "섞기 from $items" }
    delay(1000)
    logger.debug { "> 카페라떼" }
    return "카페라떼"
}