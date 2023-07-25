package dev.fastcampus.payment

import dev.fastcampus.payment.model.Product
import dev.fastcampus.payment.repository.OrderRepository
import dev.fastcampus.payment.repository.ProductRepository
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger {}

@SpringBootTest
class PaymentApplicationTests(
	@Autowired private val productRepository: ProductRepository,
	@Autowired private val orderRepository: OrderRepository,
): StringSpec({

	"context load" {

		listOf(
			Product(1,"milk", 1000),
			Product(2,"coffee", 4500),
			Product(3,"pillow", 12000),
		).let {
			productRepository.saveAll(it)
		}


		productRepository.findAll().toList().let {
			logger.debug { it }
		}




	}

})
