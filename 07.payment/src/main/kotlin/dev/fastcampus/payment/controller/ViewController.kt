package dev.fastcampus.payment.controller

import dev.fastcampus.payment.repository.ProductRepository
import dev.fastcampus.payment.service.PaymentService
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

private val logger = KotlinLogging.logger {}

@Controller
class ViewController(
    private val productRepository: ProductRepository,
    private val paymentService: PaymentService,
) {

    @GetMapping("/hello")
    suspend fun index(@RequestParam name: String?, model: Model): String {

        logger.debug { ">> name : $name" }

//        model.addAttribute("name", ReactiveDataDriverContextVariable(name))
        model.addAttribute("name", name)
        val products = productRepository.findAll().toList()
        logger.debug { ">> size : ${products.size}" }
        model.addAttribute("products", products)

        return "hello.html"
    }

    @RequestMapping("/pay")
    suspend fun pay(): String {
        return "pay.html"
    }

    @GetMapping("/payment/success")
    suspend fun isPaymentSuccessed(request: PaymentSuccess): String {
        paymentService.confirm(request)
        return "payment_success.html"
    }

    @RequestMapping("/payment/fail")
    suspend fun isPaymentFailed(request: PaymentFail): String {
        return "payment_fail.html"
    }

}

data class ReqPay(
    val userId: Long,
    val prodId: Long,
)

data class PaymentSuccess(
    val paymentType: String,
    val orderId: String,
    val paymentKey: String,
    val amount: Long,
)

data class PaymentFail(
    val code: String,
    val message: String,
    val orderId: String
)