package dev.fastcampus.coroutine.s1.thread.splitted.basic

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

//fun main() {
//    runBlocking {
//        val sum = functionC()
//        logger.debug { ">> sum : $sum" }
//    }
//}

suspend fun main() {
    val sum = functionC()
    logger.debug { ">> sum : $sum" }
}

suspend fun functionA(): Int {
    logger.debug { "tick" }
    delay(1.seconds)
    return 1
}

suspend fun functionB(): Int {
    logger.debug { "tick" }
    delay(1.seconds)
    return 2
}

suspend fun functionC(): Int {
    return functionA() + functionB()
}

/**
 * Node.js의 async / await
 *
 * 1. 비동기 처리가 기본 -> await 로 동기처리 요청
 * 2. await 처리가 안될 때가 있음

    async function print(n) {
        setTimeout(()=> console.log(n), 1000)
    }

    let departs = [];
    persons.forEach( async(person, index) => {
        val depart = await Depart.findOne({id: person.departmentId});
        departs.push(depart)
    });

 *
 * Kotlin coroutine
 *
 * 1. 동기식처럼 처리 -> launch, async 로 비동기 요청
 * 2. compiler 에서 continuation 코드 삽입 -> 예외 없이 잘 처리됨
 *
 */

// async/await 대비 장점