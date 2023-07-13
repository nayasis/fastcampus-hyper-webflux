package dev.fastcampus.useless.mvc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlockIoController {

    @GetMapping("/delay")
    fun delay(): String {
        return delay(5000L)
    }

    @GetMapping("/delay/{millisec}")
    fun delay(@PathVariable millisec: Long): String {
        return try {
            Thread.sleep(millisec)
            "delayed $millisec milli-sec"
        } catch (e: Exception) {
            "failed"
        }
    }

}