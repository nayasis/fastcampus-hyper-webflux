package dev.fastcampus.coroutine.config

import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
@Order(1)
class MdcFilter: WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid = exchange.request.headers["x-txid"]?.getOrNull(0) ?: "${UUID.randomUUID()}".replace("-","")
        MDC.put("txid", uuid)
        logger.info{ exchange.request.let { request ->
            "uri: [${request.method}] ${request.path}, ip: ${request.remoteAddress}${
                if(request.queryParams.isEmpty()) "" else ", query-param: ${request.queryParams}"
            }"
        }}
        return chain.filter(exchange).doOnError {
            ErrorConfig.mapTxid[exchange.request.id] = uuid
        }.contextWrite {
            Context.of("txid", uuid)
        }
    }
}