package dev.fastcampus.reactor.`04`.subscribe

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

fun main() {

    val customScheduler = Schedulers.newParallel("custom", 2)
//    val customScheduler = Executors.newFixedThreadPool(2).let { Schedulers.fromExecutor(it) }

    Flux.range(1,100)
        .doOnNext { logger.debug { "1st : $it" } }
        .filter { it % 2 == 0 }
        .doOnNext { logger.debug { "2nd : $it" } }
        .map { it * 3 }
        .doOnNext { logger.debug { "3rd : $it" } }
//        .publishOn(Schedulers.boundedElastic()) // 앞 쪽 체인은 그대로 두고, 뒤 쪽 체인만 지정한 worker에서 처리
        .publishOn(customScheduler)
        // publishOn -> 이후 느린 작업이 진행될 때 (I/O blocking 같은), 위쪽 main thread 를 block 시키지 않기 위함
        // 빠른 publisher, 느린 subscriber
        .filter { it % 8 == 0 }
//        .publishOn(Schedulers.boundedElastic())
        .subscribeOn(Schedulers.boundedElastic()) //
        // 앞뒤 모든 chain의 처리를 지정한 worker에서 처리 (단, publishOn 으로 세탕한 chain은 예외)
        .doOnNext { logger.debug { "4th : $it" } }
        .collectList()
        .doOnNext { logger.debug { "5th : $it" } }
//        .subscribe()
        .block()

}