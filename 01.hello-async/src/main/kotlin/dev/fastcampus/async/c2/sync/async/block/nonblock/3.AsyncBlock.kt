import mu.KotlinLogging
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

private val latchC = CountDownLatch(1)
private val latchB = CountDownLatch(1)

fun main() {
    thread { subA() }
    thread { subB() }
    thread { subC() }
}


private fun subA() {
    logger.debug { "start" }
    latchB.await()
    workHard()
    logger.debug { "end" }
}

private fun subB() {
    logger.debug { "start" }
    latchC.await()
    workHard()
    latchB.countDown()
    logger.debug { "end" }
}

private fun subC() {
    logger.debug { "start" }
    workHard()
    latchC.countDown()
    logger.debug { "end" }
}

private fun workHard() {
    Thread.sleep(1000)
}