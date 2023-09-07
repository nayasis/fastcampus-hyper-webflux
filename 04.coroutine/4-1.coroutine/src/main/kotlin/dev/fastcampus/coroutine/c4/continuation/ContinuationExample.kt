package dev.fastcampus.coroutine.c4.continuation

import kotlinx.coroutines.delay

suspend fun doA() {

    val a = 1

    println("start")

    delay(1000)

    println("sum : ${a + 1}")

    println("end")

}

suspend fun main() {
    doA()
}

//fun doA(continuation: Continuation<*>?): Any {
//    val sm = continuation as? DoAContinuation ?: DoAContinuation(continuation)
//    if(sm.label == 0) {
//        sm.a = 1
//        println("start")
//        sm.label = 1
//        if(delay(1000, continuation) == COROUTINE_SUSPENDED)
//            return COROUTINE_SUSPENDED
//    }
//    if(sm.label == 1) {
//        val a = sm.a
//        println("sum : ${a + 1}")
//        println("end")
//        return
//    }
//    error("should not be reached")
//}
//
//class DoAContinuation(continuation: Continuation<*>): Continuation<Any> {
//    var a: Int = 0
//    var label: Int = 0
//    var result: Result<Any>? = null
//    override fun resumeWith(result: Result<Any>) {
//        doA(this)
//    }
//}