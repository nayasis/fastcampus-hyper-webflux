package dev.fastcampus.webfluxcoroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    suspend fun index(): String {
        return "main page"
    }

    @GetMapping("/hello/{name}")
    suspend fun hello(@PathVariable name: String?): String {
        return "Hello $name ~"
    }

}