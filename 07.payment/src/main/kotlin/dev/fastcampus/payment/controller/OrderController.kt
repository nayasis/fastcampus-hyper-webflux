package dev.fastcampus.payment.controller

import dev.fastcampus.payment.model.Order
import dev.fastcampus.payment.service.OrderService
import dev.fastcampus.payment.service.QryOrder
import dev.fastcampus.payment.service.ResPurchaseHistory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Order {
        return orderService.get(id)
    }

    @GetMapping("/history")
    suspend fun getHistories(request: QryOrder): List<ResPurchaseHistory> {
        return orderService.retrieve(request)
    }

}