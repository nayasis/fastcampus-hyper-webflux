package dev.fastcampus.payment.controller

import dev.fastcampus.payment.service.OrderService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ViewController(
    private val orderService: OrderService
) {

    @GetMapping("/hello/{name}")
    suspend fun hello(@PathVariable name: String, model: Model): String {
        model.addAttribute("pname", name)
        model.addAttribute("order", orderService.get(1).toResOrder())
        return "hello-world.html"
    }

    @GetMapping("/pay/{orderId}")
    suspend fun pay(@PathVariable orderId: Long, model: Model): String {
        model.addAttribute("order", orderService.get(orderId))
        return "pay.html"
    }

    @GetMapping("/pay/success")
    suspend fun paySucceed(request: ReqPaySucceed): String {
        if(!orderService.authSucceed(request))
            return "pay-fail.html"
        orderService.capture(request)
        return "pay-success.html"
    }

    @GetMapping("/pay/fail")
    suspend fun payFailed(request: ReqPayFailed): String {
        orderService.authFailed(request)
        return "pay-fail.html"
    }

}

data class ReqPayFailed(
    val code: String,
    val message: String,
    val orderId: String,
)

// {paymentType=[NORMAL], orderId=[3af54beac3a44ecca5798cbdc80cba19], paymentKey=[evl2J9MNzjkYG57Eba3G4XP9aOmdwEVpWDOxmA1QXRyZ4gLw], amount=[3000]}
data class ReqPaySucceed(
    val paymentKey: String,
    val orderId: String,
    val amount: Long,
    val paymentType: TossPaymentType,
)

enum class TossPaymentType {
    NORMAL, BRANDPAY, KEYIN
}
