package dev.fastcampus.kafka

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@SpringBootTest
@Profile("test")
@ActiveProfiles("test")
@EmbeddedKafka(topics = ["test"], ports = [9092])
class ReactiveKafkaConsumerTest(
    @Autowired private val producer: ReactiveKafkaProducerTemplate<String, Person>,
): StringSpec({

    "basic" {

        val count = 10

        repeat(count) { i ->
            producer.send("test",
                Person(i,"John",31, LocalDate.now().minusYears(31))
            ).awaitSingle().also { res ->
                logger.debug { ">> send : ${res?.recordMetadata()?.offset()}" }
            }
        }

        async {
            repeat(200) {
                if(ReactiveKafkaConsumer.testResult.size == count) {
                    return@repeat
                }
                delay(10.milliseconds)
            }
        }.await()

        ReactiveKafkaConsumer.testResult.size shouldBe count

    }

})