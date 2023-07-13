package dev.fastcampus.kafka.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class TopicConfig(
    private val kafkaAdmin: KafkaAdmin
) {
    @PostConstruct
    fun init() {
        kafkaAdmin.createOrModifyTopics(
            TopicBuilder.name("test-1").partitions(1).replicas(1).build(),
            TopicBuilder.name("test-2").partitions(1).replicas(1).build(),
        )
    }
}