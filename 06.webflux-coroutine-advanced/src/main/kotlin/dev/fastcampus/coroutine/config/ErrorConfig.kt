package dev.fastcampus.coroutine.config

import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

private val logger = KotlinLogging.logger {}

@Configuration
class ErrorConfig {

    companion object {
        val mapTxid = HashMap<String,String>()
    }

    @Bean
    fun errorAttributes(): DefaultErrorAttributes {
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
                val requestId = request.exchange().request.id
                val txid = mapTxid[requestId]
                MDC.put("txid", txid)
                runCatching {
                    val e = ignoreSuppressedException(getError(request))
                    logger.error(e.message, e)
                }
                try {
                    return super.getErrorAttributes(request, options).apply {
                        logger.debug { "on error 2" }
                        val attr = this
                        // 기존 requestId 는 ObjectUtils.getIdentityHexString 에서 생성한다.
                        // 이는 request 객체를 식별하는 hashcode를 반환하는 native method 로
                        // uniqueness 를 보장하기 어렵다.
                        attr.remove("requestId")
                        attr["txid"] = txid
                    }
                } finally {
                    runCatching { mapTxid.remove(requestId) }
                    runCatching { MDC.remove("txid") }
                }
            }
        }
    }

    private fun ignoreSuppressedException(throwable: Throwable): Throwable {
        return Throwable(throwable.message).apply {
            stackTrace = throwable.stackTrace
            initCause(throwable.cause)
        }
    }

}
