package dev.fastcampus.coroutine.config.cache

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveValueOperations
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class CoroutineValueOperations (
    private val ops: ReactiveValueOperations<Any,Any>
) {
    suspend fun set(key: SimpleKey, value: Any): Boolean
        = ops.set(key,value).awaitSingle()

    suspend fun set(key: SimpleKey, value: Any, ttl: Duration): Boolean
        = ops.set(key,value,ttl.toJavaDuration()).awaitSingle()

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> get(key: SimpleKey): T? {
        return try {
            ops.get(key).awaitSingle()?.let { it as T }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun setIfAbsent(key: Any, value: Any, ttl: Duration): Boolean
        = ops.setIfAbsent(key,value,ttl.toJavaDuration()).awaitSingle()

}