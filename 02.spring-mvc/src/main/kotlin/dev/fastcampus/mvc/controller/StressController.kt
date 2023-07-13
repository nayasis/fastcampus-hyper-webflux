package dev.fastcampus.mvc.controller

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.lang.Thread.sleep

private val logger = KotlinLogging.logger {}

@RestController
class StressController {

    private val DELAY_SEC = 5

    @GetMapping("/stress/delay")
    fun delay(): String {

//        sleep(5000L)
//        return "delayed"

        logger.debug { "requested" }
        return RestTemplate().getForObject("http://localhost:8091/delay/${DELAY_SEC * 1000}", HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        })
    }

}