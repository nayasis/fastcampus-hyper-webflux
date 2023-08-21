package dev.fastcampus.kafka

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions
import java.time.LocalDate

fun main(args: Array<String>) {
	runApplication<ReactiveKafkaProducer>(*args)
}

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableKafka
class ReactiveKafkaProducer(
	private val producer: ReactiveKafkaProducerTemplate<String,Person>,
) {

	@PostConstruct
	fun init() {
		runBlocking(Dispatchers.IO) {
			repeat(1) {i ->
				producer.send("test",
					Person(i,"John",31,LocalDate.now().minusYears(31))
				).awaitSingle().also { res ->
					logger.debug { ">> send : ${res?.recordMetadata()?.offset()}" }
				}
			}
		}
	}

}

@Configuration
class ProducerConfig {
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
	val id: Int,
	val name: String,
	val age: Int,
	val birthDay: LocalDate,
)