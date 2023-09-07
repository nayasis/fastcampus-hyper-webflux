package dev.fastcampus.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions
import java.time.LocalDate
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
	private val consumer: ReactiveKafkaConsumerTemplate<String,Person>,
	private val producer: ReactiveKafkaProducerTemplate<String,Person>,
	environment: Environment,
) {

	private val onTest = "test" in environment.activeProfiles

	@PostConstruct
	fun init() {
		consumer.receiveAutoAck()
//            .doOnNext {
//                runBlocking(Dispatchers.IO) {
//                    executor(it)
//                }
//            }
			.flatMap { it ->
				mono {
					executor(it)
				}.thenMany(Flux.just(it))
			}
			.subscribe()
		logger.debug { ">> ready to consume" }
	}

	suspend fun executor(record: ConsumerRecord<String, Person>) {
		if( alreadyReceived(record) ) return
		try {
			logger.debug { "> topic=${record.topic()}, partition=${record.partition()} offset=${record.offset()}" }
			executor(record.value(), record)
		} catch (e: Exception) {
			logger.error(e.message, e)
			producer.send("test", record.value()).awaitSingle()
		}
	}

	suspend fun alreadyReceived(record: ConsumerRecord<String, Person>): Boolean {
		if(onTest) return false
		val offset = "${record.topic()}-${record.partition()}-${record.offset()}"
		return if(redisTemplate.opsForValue().setIfAbsent(offset, true) == true) {
			redisTemplate.expire(offset, 10.minutes.toJavaDuration())
			false
		} else {
			logger.debug { ">> already consumed !!" }
			true
		}
	}

suspend fun executor(person: Person, record: ConsumerRecord<String, Person>) {
		if(Random.nextInt(3) == 0) {
			throw RuntimeException("it is test error ! (topic=${record.topic()}, partition=${record.partition()} offset=${record.offset()})")
		} else {
			logger.debug { "success : $person (topic=${record.topic()}, partition=${record.partition()} offset=${record.offset()})" }
		}

		if(onTest) {
			testResult.add(person.id)
		}
	}

	companion object {
		val testResult = ArrayList<Int?>()
	}

}

@Configuration
class Config(
	private val kafkaAdmin: KafkaAdmin,
) {

	@PostConstruct
	fun init() {
		kafkaAdmin.createOrModifyTopics(
			TopicBuilder.name("test").partitions(1).replicas(1).build()
		)
	}

	@Bean
	fun reactiveConsumer(properties: KafkaProperties): ReactiveKafkaConsumerTemplate<String,Person> {
		return properties.buildConsumerProperties()
			.let { prop -> ReceiverOptions.create<String,Person>(prop) }
			.let { option -> option.subscription(listOf("test")) } // 구독할 topic을 적어준다.
			.let { option ->
				ReactiveKafkaConsumerTemplate(option)
			}
	}

	@Bean
	fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, Person> {
		return properties.buildProducerProperties()
			.let { prop ->
				SenderOptions.create<String,Person>(prop)
			}
			.let { option -> ReactiveKafkaProducerTemplate(option) }
	}

}

data class Person(
	var id: Int? = null,
	var name: String? = null,
	var age: Int? = null,
	var birthDay: LocalDate? = null,
)