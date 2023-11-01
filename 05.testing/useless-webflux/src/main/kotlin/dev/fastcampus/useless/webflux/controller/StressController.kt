package dev.fastcampus.useless.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StressController {

    @GetMapping("/delay")
    suspend fun delay(): String {
        return delay(5000L)
    }

    @GetMapping("/delay/{millisec}")
    suspend fun delay(@PathVariable millisec: Long): String {
        return try {
            kotlinx.coroutines.delay(millisec)
            "delayed $millisec milli-sec"
        } catch (e: Exception) {
            "failed"
        }
    }

    @PostMapping("/sample")
    suspend fun sample(
        @RequestHeader name: String?,
        @RequestHeader age: Int?,
    ): ResTest {
        return ResTest(
            name ?: "John doe",
            age ?: 20,
        )
    }

    @GetMapping("/fail")
    suspend fun fail(@RequestParam fail: Boolean?): String {
        if(fail == true) {
            throw RuntimeException("Test fail !!")
        } else {
            return "success"
        }
    }

}

data class ResTest(
    val name: String,
    val age: Int,
)