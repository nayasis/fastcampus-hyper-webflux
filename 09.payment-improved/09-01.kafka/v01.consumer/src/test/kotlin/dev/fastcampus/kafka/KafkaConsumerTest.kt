package dev.fastcampus.kafka

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka

private val logger = KotlinLogging.logger {}

@SpringBootTest
@EmbeddedKafka(topics = ["test-1","test-2"], ports = [9092])
class KafkaConsumerTest(
    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @Test
    fun basic() {

        repeat(10) { i ->
            val msg = "test message : $i"
            kafkaTemplate.send("test-1", msg).get().recordMetadata.also {
                logger.debug { ">> send : $msg (topic:${it.topic()}, partition:${it.partition()}, offset: ${it.offset()})" }
            }
        }
        Thread.sleep(10_000)
    }

}