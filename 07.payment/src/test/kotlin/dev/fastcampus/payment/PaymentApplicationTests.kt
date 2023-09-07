package dev.fastcampus.payment

import dev.fastcampus.payment.common.rollback
import dev.fastcampus.payment.model.Product
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.ProductRepository
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

private val logger = KotlinLogging.logger {}

@SpringBootTest
class PaymentApplicationTests(
	@Autowired private val productRepository: ProductRepository,
	@Autowired private val orderRepository: OrderRepository,
	@Autowired private val rxtx: TransactionalOperator,
): StringSpec({

	"context load".config(false) {
		rxtx.rollback { tx ->
			listOf(
				Product("coffee", 4500),
				Product("pillow", 12000),
			).let {
				productRepository.saveAll(it).last()
			}
			productRepository.findAll().toList().let {
				logger.debug { it.joinToString("\n","\n") }
			}
		}
	}

})
