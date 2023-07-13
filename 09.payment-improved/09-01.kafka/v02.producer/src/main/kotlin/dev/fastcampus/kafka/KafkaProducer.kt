package dev.fastcampus.kafka

import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun main(args: Array<String>) {
	runApplication<KafkaProducer>(*args)
}

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableKafka
class KafkaProducer(
	private val kafkaTemplate: KafkaTemplate<String, String>,
): CommandLineRunner {

	override fun run(vararg args: String?) {
		val message = "sent by application ! ${LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)}"
		kafkaTemplate.send("test-1", message)
		logger.debug { ">> send message : $message" }
	}
}