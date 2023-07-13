package dev.fastcampus.coroutine.controller

import dev.fastcampus.coroutine.service.ExternalApi
import dev.fastcampus.coroutine.service.ResTest
import dev.fastcampus.coroutine.service.TestService
import dev.fastcampus.coroutine.service.ifNull
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val testService: TestService,
    private val externalApi: ExternalApi,
) {

    @GetMapping("/test/mdc")
    suspend fun mdc(): String? {
        return testService.testMdcLogging()
//        return withContext(MDCContext()) {
//            testService.testMdcLogging()
//        }
    }

    @GetMapping("/external/hello")
    suspend fun hello(): ResTest {
        return externalApi.hello()
    }

    @GetMapping("/external/world")
    suspend fun world(): ResTest {
        return externalApi.world()
    }

    @GetMapping("/circuit")
    suspend fun circuitBreaker(@RequestParam fail: Boolean?): String {
        return externalApi.fail(fail)
    }

}