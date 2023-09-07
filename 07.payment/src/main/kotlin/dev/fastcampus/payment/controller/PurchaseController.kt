package dev.fastcampus.payment.controller

import dev.fastcampus.payment.service.PurchaseService
import dev.fastcampus.payment.service.QrySearch
import dev.fastcampus.payment.service.ResSearch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/purchase")
class PurchaseController(
    private val purchaseService: PurchaseService,
) {

    @GetMapping("/history")
    suspend fun get(request: QrySearch): ResSearch {
        return purchaseService.search(request)
    }

}