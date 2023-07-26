package dev.fastcampus.payment.controller

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class TestApiController {

    @GetMapping("/api/sample")
    suspend fun sample(@RequestParam name: String?): String {

        logger.debug { ">> name : $name" }

        return "hello $name"
    }

}