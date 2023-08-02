package dev.fastcampus.coroutine.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.domain.Range
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.connection.RedisGeoCommands.*
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    @Autowired private val redisTemplate: ReactiveRedisTemplate<Any,Any>
): StringSpec({

    val KEY = "test-key"

    "hello world" {
        val ops = redisTemplate.opsForValue()
        ops.delete(KEY).awaitSingle()
        ops.set(KEY, "merong").awaitSingle()
        ops.get(KEY).awaitSingle() shouldBe "merong"
    }

    "list" {

        val ops = redisTemplate.opsForList()
        redisTemplate.delete(KEY).awaitSingle()

        val size = ops.rightPushAll(KEY, 0,1,2,3,4,5,6,7,8,9).awaitSingle()

        size shouldBe 10
        redisTemplate.type(KEY).awaitSingle() shouldBe DataType.LIST
        ops.size(KEY).awaitSingle() shouldBe 10

        ops.range(KEY, 0,10).toStream().forEach { logger.debug { it } }

        ops.rightPop(KEY).awaitSingle() shouldBe 9
        ops.rightPop(KEY).awaitSingle() shouldBe 8
        ops.leftPop(KEY).awaitSingle() shouldBe 0
        ops.leftPop(KEY).awaitSingle() shouldBe 1

        // expire는 key가 있어야지만 동작한다.
        redisTemplate.expire(KEY, 3.seconds.toJavaDuration()).subscribe()
        delay(5.seconds)
        val list = redisTemplate.opsForList().range(KEY, 0, 10).collectList().awaitSingle()
            .also { logger.debug { it } }
        list.size shouldBe 0

    }

    "hash" {

        val ops = redisTemplate.opsForHash<String,String>()
        ops.delete(KEY).awaitSingle()

        val map = (1..10).map { "$it" to "val-$it" }.toMap()

        ops.putAll(KEY, map).awaitSingle()
        redisTemplate.expire(KEY, 3.seconds.toJavaDuration()).subscribe()

        redisTemplate.type(KEY).awaitSingle() shouldBe DataType.HASH
        ops.size(KEY).awaitSingle() shouldBe 10
        ops.get(KEY,"5").awaitSingle() shouldBe "val-5"
        ops.remove(KEY,"5").awaitSingle() shouldBe 1
        ops.size(KEY).awaitSingle() shouldBe 9

        delay(5.seconds)

        ops.size(KEY).awaitSingle() shouldBe 0

    }

    "sorted set" {
        val ops = redisTemplate.opsForZSet().also { it.delete(KEY).awaitSingle() }

        for(i in 1..100) {
            ops.add(KEY, "$i", Date().time * (-1.0)).awaitSingle()
            ops.removeRange(KEY, Range.closed(10L, -1L)).awaitSingle()
            ops.range(KEY, Range.closed(0L,-1L)).collectList().awaitSingle().let { logger.debug { it } }
        }

    }

    "geo redis" {
        val ops = redisTemplate.opsForGeo().also { it.delete(KEY).awaitSingle() }

        // 이름/경도(longitude)/위도(latitude)
        ops.add(KEY, GeoLocation("seoul",   Point(126.97806, 37.56667))).awaitSingle()
        ops.add(KEY, GeoLocation("busan",   Point(129.07556, 35.17944))).awaitSingle()
        ops.add(KEY, GeoLocation("incheon", Point(126.70528, 37.45639))).awaitSingle()
        ops.add(KEY, GeoLocation("daegu",   Point(128.60250, 35.87222))).awaitSingle()
        ops.add(KEY, GeoLocation("anyang",  Point(126.95556, 37.39444))).awaitSingle()
        ops.add(KEY, GeoLocation("daejeon", Point(127.38500, 36.35111))).awaitSingle()
        ops.add(KEY, GeoLocation("gwangju", Point(126.85306, 35.15972))).awaitSingle()
        ops.add(KEY, GeoLocation("suwon",   Point(127.02861, 37.26389))).awaitSingle()

        ops.distance(KEY, "seoul", "busan").awaitSingle().let { logger.debug { "seoul -> busan : ${it}" } }

        val position = ops.position(KEY,"seoul").awaitSingle()
        val circle = Circle(position!!, Distance(100.0, Metrics.KILOMETERS))

        ops.radius(KEY,circle).collectList().awaitSingle().map {it.content.name}.let {
            logger.debug { "close city near seoul : $it" }
            it.size shouldBe 4
        }

    }

    "hyper loglog" {
        val ops = redisTemplate.opsForHyperLogLog().also { it.delete(KEY).awaitSingle() }

        val logs = (1..100_000).map { "$it" }.toTypedArray()
        ops.add(KEY, *logs).awaitSingle()

        // 원소 개수는 추정치이므로 정확하지 않다.
        ops.size(KEY).awaitSingle().let { logger.debug { it } }

    }


})