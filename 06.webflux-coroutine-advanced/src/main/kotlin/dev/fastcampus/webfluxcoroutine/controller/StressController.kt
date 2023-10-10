package dev.fastcampus.webfluxcoroutine.controller

import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

private val logger = KotlinLogging.logger {}

@RestController
class StressController(
    @Value("\${api.externalUrl}")
    private val externalUrl: String
) {

    private val client = WebClient.builder().baseUrl(externalUrl)
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .build()

    @GetMapping("/stress/delay")
    suspend fun delay(): String {
        logger.debug { "requested" }
        return client.get().uri("/delay").retrieve().bodyToMono<String>().awaitSingle()
    }
}