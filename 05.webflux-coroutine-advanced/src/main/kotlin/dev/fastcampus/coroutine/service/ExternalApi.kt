package dev.fastcampus.coroutine.service

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {  }

@Service
class ExternalApi {

    private val webclient = WebClient.builder().baseUrl("http://localhost:8091").build()

    val circuitBreaker = CircuitBreaker.of("sample", CircuitBreakerConfig {
        slidingWindowSize(10)
        failureRateThreshold(20.0F)
        waitDurationInOpenState(10.seconds.toJavaDuration())
        permittedNumberOfCallsInHalfOpenState(3)
//            waitDurationInOpenState(Duration.ofSeconds(30))
    })

    // 1초에 2번
    val rateLimiter = RateLimiter.of("rps-limiter", RateLimiterConfig.custom()
        .limitForPeriod(2)
        .limitRefreshPeriod(10.seconds.toJavaDuration())
//        .timeoutDuration(Duration.ofSeconds(3))
        .build())

    suspend fun hello(): ResTest {
        return webclient.post().uri("/test")
            .header("name", "hello")
            .retrieve().awaitBody()
    }

    suspend fun world(): ResTest {
        return webclient.post().uri("/test")
            .header("age", "10")
            .retrieve().awaitBody()
    }

    suspend fun fail(fail: Boolean?): String {
        return rateLimiter.executeSuspendFunction { circuitBreaker.executeSuspendFunction {
            logger.debug { ">>> executed !" }
            webclient.get().uri { builder -> builder
                .path("/fail")
                .queryParamIfPresent("fail", Optional.ofNullable(fail) )
                .build()
            }.retrieve().awaitBody()
        }}
    }

}

data class ResTest(
    val name: String,
    val age: Int,
    val birthDate: LocalDateTime
)