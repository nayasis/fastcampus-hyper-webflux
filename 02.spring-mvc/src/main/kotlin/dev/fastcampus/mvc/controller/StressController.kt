package dev.fastcampus.mvc.controller

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

private val logger = KotlinLogging.logger {}

@RestController
class StressController(
    @Value("\${biz.api.external}")
    private val external: String,
) {

    private val DELAY_SEC = 5

    @GetMapping("/stress/delay")
    fun delay(): String {

//        sleep(5000L)
//        return "delayed"

        logger.debug { "requested" }
        return RestTemplate().getForObject("${external}/delay/${DELAY_SEC * 1000}", HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })
    }

}