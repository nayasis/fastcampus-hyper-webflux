package dev.fastcampus.coroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld {

    @GetMapping("/")
    suspend fun index(): String {
        return "Hello coroutine"
    }

}