package dev.fastcampus.payment.service

import com.fasterxml.jackson.databind.ObjectMapper
import dev.fastcampus.payment.common.KafkaProducer
import dev.fastcampus.payment.model.Order
import org.springframework.stereotype.Service

val TOPIC_PAYMENT = "payment"

@Service
class KafkaPipeline(
    private val producer: KafkaProducer,
    private val mapper: ObjectMapper,
) {
    suspend fun sendPayment(order: Order) {
        mapper.writeValueAsString(order).let { json ->
            producer.send(TOPIC_PAYMENT, json)
        }
    }
}