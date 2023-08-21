package dev.fastcampus.payment.controller

import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.model.code.TxStatus
import dev.fastcampus.payment.repository.ProductRepository
import dev.fastcampus.payment.service.OrderService
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
    private val orderService: OrderService,
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

    @GetMapping("/pay")
    suspend fun pay(reqPay: ReqPay, model: Model): String {
        val order = orderService.create(reqPay.userId, reqPay.prodId)
        model.addAttribute("order", ResOrder.fromOrder(order))
        return "pay.html"
    }

    @GetMapping("/pay/success")
    suspend fun isPaymentSuccessed(request: PaymentSuccess, model: Model): String {
        return if(paymentService.confirm(request)) "pay_success.html" else {
            "pay_fail.html"
        }
    }

    @RequestMapping("/pay/fail")
    suspend fun isPaymentFailed(request: PaymentFail): String {
        return "pay_fail.html"
    }

}

data class ResOrder(
    val userId: String,
    val orderId: String,
    val description: String,
    val amount: Long,
    val status: TxStatus,
) {
     companion object {
         suspend fun fromOrder(order: Order): ResOrder {
             return ResOrder(
                 "user-${order.userId}",
                 order.paymentOrderId ?: "",
                 order.getProduct()?.name ?: "",
                 order.amount,
                 order.status,
             )
         }
     }
}

data class ReqPay(
    val userId: Long,
    val prodId: Long,
)

data class PaymentSuccess(
    val paymentType: String,
    var orderId: String,
    val paymentKey: String,
    val amount: Long,
)

data class PaymentFail(
    val code: String,
    val message: String,
    val orderId: String
)