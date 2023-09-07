package dev.fastcampus.payment.service

import io.kotest.core.spec.style.StringSpec
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class PurchaseServiceTest(
    @Autowired private val purchaseService: PurchaseService,
): StringSpec({
    "search" {
        purchaseService.search(QrySearch(userId = 1)).let {
            logger.debug { it }
        }
    }
})