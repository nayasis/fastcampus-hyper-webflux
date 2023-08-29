package dev.fastcampus.coroutine.redis.pubsub

import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class RedisPubSubController(
    private val redisTemplate: ReactiveRedisTemplate<Any,Any>
): ApplicationListener<ApplicationReadyEvent> {

    private val TOPIC_SAMPLE = "sample-topic"

    @PostMapping("/send/{topic}/{message}")
    suspend fun publishToRedis(@PathVariable topic: String, @PathVariable message: String) {
        logger.debug { ">> send : ${topic}::${message}" }
        redisTemplate.convertAndSend(topic, message).awaitSingle()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        redisTemplate.listenToChannel(TOPIC_SAMPLE).doOnNext {
            logger.debug { ">> receive : $it}" }
        }.subscribe()
    }

}