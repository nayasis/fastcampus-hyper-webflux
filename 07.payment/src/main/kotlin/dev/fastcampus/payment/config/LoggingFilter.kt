package dev.fastcampus.payment.config

import mu.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
@Order(2)
class LoggingFilter: WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestLoggingDecorator = object: ServerHttpRequestDecorator(exchange.request) {
            override fun getBody(): Flux<DataBuffer> {
                // ReactorServerHttpRequest에서 이미 request body contents를 읽어와 캐시해 놓았음
                return super.getBody().doOnNext { dataBuffer ->
                    logger.info {
                        val requestBody = ByteArrayOutputStream().use { outputStream ->
                            Channels.newChannel(outputStream)
                                .write(dataBuffer.readableByteBuffers().next())
                            String(outputStream.toByteArray(), Charsets.UTF_8)
                        }
                        "payload: $requestBody"
                    }
                }
            }
        }
        return chain.filter(
            exchange.mutate().request(requestLoggingDecorator).build()
        )
    }

}