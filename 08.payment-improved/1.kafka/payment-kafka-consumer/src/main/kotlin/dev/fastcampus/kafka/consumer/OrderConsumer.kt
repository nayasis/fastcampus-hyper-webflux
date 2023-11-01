package dev.fastcampus.kafka.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import dev.fastcampus.kafka.config.Consumer
import dev.fastcampus.kafka.config.TOPIC_PAYMENT
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class OrderConsumer(
    private val consumer: Consumer,
    private val historyApi: HistoryApi,
    private val mapper: ObjectMapper,
): InitializingBean {

    override fun afterPropertiesSet() {

        consumer.consume(TOPIC_PAYMENT,"history") { record ->
            mapper.readValue(record.value(), Order::class.java).let { order ->
                logger.debug { ">> history: ${order}" }
                historyApi.save(order)
            }
        }

        var totalSum = 0L
        consumer.consume(TOPIC_PAYMENT,"sum") { record ->
            mapper.readValue(record.value(), Order::class.java).let { order ->
                totalSum += order.amount
                logger.debug { ">> total sum: ${totalSum}" }
            }
        }

        logger.debug { ">> ready consumer" }
    }
}