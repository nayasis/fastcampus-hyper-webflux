package dev.fastcampus.coroutine.config.lock

import dev.fastcampus.coroutine.config.cache.CoroutineValueOperations
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger{}

@Component
class Locker(
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {

    private val ops = CoroutineValueOperations(redisTemplate.opsForValue())

    suspend fun <T> lock(key: Any, fn: suspend () -> T): T {
        if( ! obtainLock(key) )
            throw TimeoutException("fail to obtain lock (key: $key)")
        try {
            return fn()
        } finally {
            unlock(key)
        }
    }

    private suspend fun obtainLock(key: Any): Boolean {
        val start = System.nanoTime()
        while(! ops.setIfAbsent(key.toLockKey(), "lock",3.minutes)) {
            delay(100.milliseconds)
            if((System.nanoTime() - start).nanoseconds >= 3.seconds)
                return false
        }
        return true
    }

    private suspend fun unlock(key: Any) {
        redisTemplate.delete(key.toLockKey()).awaitSingle()
    }

    private fun Any.toLockKey(): SimpleKey {
        return SimpleKey(Locker::class.simpleName, this)
    }

}

