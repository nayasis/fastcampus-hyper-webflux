package dev.fastcampus.coroutine.c4.continuation

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

suspend fun workHard() {
    logger.debug { "start hard work" }
    delay(1.seconds)
    logger.debug { "work done" }
}

suspend fun main() {
    workHard()
}

//fun workHard(continuation: Continuation<*>?): Any {
//    val sm = continuation as? WorkHardContinuation ?: WorkHardContinuation(continuation)
//    if(sm.label == 0) {
//        logger.debug { "start hard word" }
//        sm.label = 1
//        if(delay(1.seconds, continuation) == COROUTINE_SUSPENDED)
//            return COROUTINE_SUSPENDED
//    }
//    if(sm.label == 1) {
//        logger.debug { "work done" }
//        return
//    }
//    error("should not be reached")
//
//}
//
//class WorkHardContinuation(continuation: Continuation<*>): Continuation<Unit> {
//    override fun resumeWith(result: Result<Unit>) {
//        workHard(this)
//    }
//}
