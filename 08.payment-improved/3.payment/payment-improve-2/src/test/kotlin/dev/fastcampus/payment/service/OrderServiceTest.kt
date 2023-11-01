package dev.fastcampus.payment.service

import dev.fastcampus.payment.config.WithRedisContainer
import dev.fastcampus.payment.controller.ReqPaySucceed
import dev.fastcampus.payment.controller.TossPaymentType
import dev.fastcampus.payment.exception.NoProductFound
import dev.fastcampus.payment.model.PgStatus
import dev.fastcampus.payment.model.Product
import dev.fastcampus.payment.repository.ProductInOrderRepository
import dev.fastcampus.payment.repository.ProductRepository
import dev.fastcampus.payment.service.api.ResConfirm
import dev.fastcampus.payment.service.api.TossPayApi
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mu.KotlinLogging
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test", "toss-pay-test")
class OrderServiceTest(
    @Autowired orderService: OrderService,
    @Autowired paymentService: PaymentService,
    @Autowired productRepository: ProductRepository,
    @Autowired productInOrderRepository: ProductInOrderRepository,
    @Autowired tossPayApi: TossPayApi,
): WithRedisContainer, StringSpec({

    beforeSpec {
        productRepository.save(Product(1,"apple", 1000).apply { new = true })
        productRepository.save(Product(2,"banana", 1200).apply { new = true })
        productRepository.save(Product(3,"mango", 700).apply { new = true })
        productRepository.save(Product(4,"orange", 2100).apply { new = true })
    }

    "create on fail" {
        val request = ReqCreateOrder(11, listOf(
            ReqProdQuantity(1,1),
            ReqProdQuantity(2,2),
            ReqProdQuantity(3,3),
            ReqProdQuantity(4,4),
            ReqProdQuantity(5,5),
        ))

        shouldThrow<NoProductFound> {
            orderService.create(request)
        }
//        val order = orderService.create(request).also { logger.debug { it } }
    }

    "create" {
        val request = ReqCreateOrder(11, listOf(
            ReqProdQuantity(1,1),
            ReqProdQuantity(2,2),
            ReqProdQuantity(3,3),
            ReqProdQuantity(4,4),
        ))

        val order = orderService.create(request).also { logger.debug { it } }

        order.amount shouldBe 13900
        order.description shouldNotBe null
        order.pgOrderId shouldNotBe null

        productInOrderRepository.countByOrderId(order.id) shouldBe 4

    }

    "capture success" {
        val order = orderService.create(ReqCreateOrder(11, listOf(ReqProdQuantity(1,10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed("test idempotency key", order.pgOrderId!!, order.amount, TossPaymentType.NORMAL)
        paymentService.authSucceed(token)
        val orderAuthed = orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token)).thenReturn(
            ResConfirm(
                paymentKey = orderAuthed.pgKey!!,
                orderId = orderAuthed.pgOrderId!!,
                status = "Done",
                totalAmount = orderAuthed.amount,
                method = "card"
            )
        )

        paymentService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_SUCCESS }

    }

    "capture retry" {
        val order = orderService.create(ReqCreateOrder(11, listOf(ReqProdQuantity(1,10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed("test idempotency key", order.pgOrderId!!, order.amount, TossPaymentType.NORMAL)
        paymentService.authSucceed(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token)).thenThrow(
            WebClientRequestException::class.java
        )

        paymentService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_RETRY }

    }

    "capture fail" {
        val order = orderService.create(ReqCreateOrder(11, listOf(ReqProdQuantity(1,10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed("test idempotency key", order.pgOrderId!!, order.amount, TossPaymentType.NORMAL)
        paymentService.authSucceed(token)
        val orderAuthed = orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token)).thenThrow(
            WebClientResponseException::class.java
        )

        paymentService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_FAIL }

    }

})

@Configuration
@Profile("toss-pay-test")
class TossPayTestConfig {
    @Bean
    @Primary
    fun testTossPayApi(): TossPayApi {
        return Mockito.mock(TossPayApi::class.java)
    }
}
