package dev.fastcampus.payment.service

import dev.fastcampus.payment.model.PurchaseHistory
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderResult

@Service
class KafkaService(
    private val producer: ReactiveKafkaProducerTemplate<String, PurchaseHistory>,
    @Value("\${payment.kafka.topic}")
    private val topic: String,
) {
    suspend fun send(history: PurchaseHistory): SenderResult<Void> {
        return producer.send(topic, history).awaitSingle()
    }
}

@Configuration
class KafkaConfig {
    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, PurchaseHistory> {
        return properties.buildProducerProperties()
            .let { prop ->
                SenderOptions.create<String,PurchaseHistory>(prop)
            }
            .let { option -> ReactiveKafkaProducerTemplate(option) }
    }
}