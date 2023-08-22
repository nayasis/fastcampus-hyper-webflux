package dev.fastcampus.payment.service

import dev.fastcampus.payment.model.PurchaseHistory
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.repository.PurchaseHistoryRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class KafkaServiceTest(
    @Autowired private val kafkaService: KafkaService,
    @Autowired private val purchaseHistoryRepository: PurchaseHistoryRepository,
): StringSpec({
    "send" {
        val id = 2L
        kafkaService.send(PurchaseHistory(
            orderId = id,
            userId =  1,
            prodId = 1,
            prodNm = "연동테스트 1",
            description = "연동 1",
            amount = 9644,
            status = TxStatus.NEED_CHECK,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        ))
        delay(5_000)
        purchaseHistoryRepository.findById(id).let {
            logger.debug { ">> saved in elastic search: $it" }
        }
    }
})
