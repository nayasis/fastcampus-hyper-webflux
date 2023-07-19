package dev.fastcampus.coroutine.controller

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

private val logger = KotlinLogging.logger {}

@RestController
class StressController {

    private val client = createWebClient()

    private fun createWebClient(): WebClient {
        val provider = ConnectionProvider.builder("stress")
            .maxConnections(20_000)
            .pendingAcquireTimeout(Duration.ofSeconds(120))
            .build()
        val connector = ReactorClientHttpConnector(reactor.netty.http.client.HttpClient.create(provider))
        return WebClient.builder()
            .baseUrl("http://localhost:8091")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(connector)
            .build()
    }

    private val DELAY_SEC = 5

    @GetMapping("/stress/delay")
    suspend fun delay(): Mono<String> {
//        kotlinx.coroutines.delay(5000L)
//        return Mono.just("delayed")

        logger.debug { "requested" }
        return client.get().uri("/delay/${DELAY_SEC * 1000}").retrieve().bodyToMono(String::class.java)
    }

}