package dev.fastcampus.springmvc.controller

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

private val logger = KotlinLogging.logger {  }

@RestController
class StressController(
    @Value("\${api.externalUrl}")
    private val externalUrl: String
) {

    private val restTemplate = RestTemplate()

    @GetMapping("/stress/delay")
    fun delay(): String {
        logger.debug { "requested" }
        return restTemplate.getForObject("${externalUrl}/delay", HttpHeaders().apply {
            contentType = APPLICATION_JSON
        })
    }
}