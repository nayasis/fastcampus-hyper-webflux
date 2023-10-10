package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.config.validator.DateString
import dev.fastcampus.webfluxcoroutine.exception.ExternalApi
import dev.fastcampus.webfluxcoroutine.exception.InvalidParameter
import dev.fastcampus.webfluxcoroutine.service.Account
import dev.fastcampus.webfluxcoroutine.service.AccountService
import dev.fastcampus.webfluxcoroutine.service.AdvancedService
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val service: AdvancedService,
    private val externalApi: ExternalApi,
    private val accountService: AccountService,
) {

    @GetMapping("/test/mdc")
    suspend fun mdc() {
        logger.debug { "start MDC TxId" }
        delay(100)
        service.mdc1()
        logger.debug { "end MDC TxId" }
    }

    @PutMapping("/test/error")
    suspend fun error(@RequestBody @Valid request: ReqErrorTest) {
        if(request.message == "error") {
            throw InvalidParameter(request, request::message,code = "custom code", message = "custom error")
        }
//        throw RuntimeException("yahoo !")
    }

    @GetMapping("/exteranl/delay")
    suspend fun delay() {
        externalApi.delay()
    }

    @GetMapping("/external/circuit/{flag}", "/external/circuit", "/external/circuit/" )
    suspend fun testCircuitBreaker(@PathVariable flag: String): String {
        return externalApi.testCircuitBreaker(flag)
    }

    @GetMapping("/account/{id}")
    suspend fun getAccount(@PathVariable id: Long): Account {
        return accountService.get(id)
    }

    @PutMapping("/account/{id}/{amount}")
    suspend fun deposit(@PathVariable id: Long, @PathVariable amount: Long): Account {
        return accountService.deposit(id,amount)
    }

}

data class ReqErrorTest (
    @field:NotEmpty
    @field:Size(min=3, max = 10)
    val id: String?,
    @field:NotNull
    @field:Positive(message = "양수만 입력 가능")
    @field:Max(100)
    val age: Int?,
    @field:DateString
    val birthday: String?,

    val message: String? = null

)

