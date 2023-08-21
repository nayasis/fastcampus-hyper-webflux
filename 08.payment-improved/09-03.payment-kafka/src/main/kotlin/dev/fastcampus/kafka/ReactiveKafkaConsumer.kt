package dev.fastcampus.kafka

import dev.fastcampus.kafka.model.PurchaseHistory
import dev.fastcampus.kafka.repository.PurchaseHistoryRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
	runApplication<ReactiveKafkaConsumer>(*args)
}

@SpringBootApplication
@EnableKafka
class ReactiveKafkaConsumer(
	private val redisTemplate: RedisTemplate<Any, Any>,
	private val consumer: ReactiveKafkaConsumerTemplate<String,PurchaseHistory>,
	private val producer: ReactiveKafkaProducerTemplate<String,PurchaseHistory>,
	private val repository: PurchaseHistoryRepository,
	@Value("\${payment.kafka.topic}")
	private val topic: String,
): ApplicationListener<ApplicationReadyEvent> {

	suspend fun executor(record: ConsumerRecord<String, PurchaseHistory>) {
		if( alreadyReceived(record) ) return
		try {
			logger.debug { "> topic: ${record.topic()}, partition: ${record.partition()} offset: ${record.offset()}, order: ${record.value().orderId}" }
			repository.save(record.value())
		} catch (e: Exception) {
			logger.error(e.message, e)
			producer.send(topic, record.value()).awaitSingle()
		}
	}

	suspend fun alreadyReceived(record: ConsumerRecord<String, PurchaseHistory>): Boolean {
		val offset = "$topic::${record.topic()}-${record.partition()}-${record.offset()}"
		return if(redisTemplate.opsForValue().setIfAbsent(offset, true) == true) {
			redisTemplate.expire(offset, 10.minutes.toJavaDuration())
			false
		} else {
			true
		}
	}

	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		consumer.receiveAutoAck()
			.flatMap { it ->
				mono { executor(it) }.thenMany(Flux.just(it))
			}.subscribe()
		logger.debug { ">> ready to consume" }
	}

}

@Configuration
class Config(
	private val kafkaAdmin: KafkaAdmin,
	@Value("\${payment.kafka.topic}")
	private val topic: String,
): ApplicationListener<ApplicationReadyEvent> {

	@Bean
	fun reactiveConsumer(properties: KafkaProperties): ReactiveKafkaConsumerTemplate<String,PurchaseHistory> {
		return properties.buildConsumerProperties()
			.let { prop -> ReceiverOptions.create<String,PurchaseHistory>(prop) }
			.let { option -> option.subscription(listOf(topic)) }
			.let { option ->
				ReactiveKafkaConsumerTemplate(option)
			}
	}

	@Bean
	fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, PurchaseHistory> {
		return properties.buildProducerProperties()
			.let { prop ->
				SenderOptions.create<String,PurchaseHistory>(prop)
			}
			.let { option -> ReactiveKafkaProducerTemplate(option) }
	}

	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		kafkaAdmin.createOrModifyTopics(
			TopicBuilder.name(topic).partitions(1).replicas(1).build()
		)
	}

}