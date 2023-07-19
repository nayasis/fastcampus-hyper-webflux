package dev.fastcampus.coroutine.c7.launch.async

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val logger = KotlinLogging.logger {}

// sequence
// 세수 -> 물 끓이기 -> 커피 만들기 -> 빵 만들기 -> 아침 먹기

suspend inline fun task(name: String, duration: Duration, action: (() -> Unit) = {}) {
    logger.debug { ">> $name : start" }
    action.invoke()
    delay(duration)
    logger.debug { ">> $name : end" }
}

suspend fun washFace() {
    task("세수", 3.seconds)
}

suspend fun boilWater() {
    task("물 끓이기", 3.seconds)
}

suspend fun makeCoffee(): String {
    task("커피 만들기", 2.seconds)
    return "카페라떼"
}

suspend fun makeBread(): String {
    task("빵 만들기", 2.seconds)
    return "베이글"
}

suspend fun eatBreakfast(foods: List<String>) {
    task("아침 먹기", 2.seconds) {
        logger.debug { "$foods" }
    }
}

suspend fun main() {
    coroutineScope {
        launch { boilWater() }
        launch { washFace() }
    }
    coroutineScope {
        val onCoffee = async { makeCoffee() }
        val onBread = async { makeBread() }
        eatBreakfast(listOf(onCoffee.await(),onBread.await()))
    }
}



