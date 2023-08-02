package dev.fastcampus.kafka.service

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener

private val logger = KotlinLogging.logger {}

@Configuration
class KafkaListener(
    private val streamsBuilder: StreamsBuilder
) {

    @PostConstruct
    fun consumeTest1() {
        streamsBuilder.stream(
            "test-1", Consumed.with(Serdes.String(),Serdes.String())
        ).peek { key, value ->
            logger.debug { "> got test-1 : $value" }
        }.mapValues { message ->
            "through !!! -> ${message.uppercase()}"
        }.to("test-2", Produced.with(Serdes.String(), Serdes.String()))

    }

    @KafkaListener(topics = ["test-2"])
    fun consumeTest2(message: String) {
        logger.debug { "> got test-2 : $message" }
    }

}