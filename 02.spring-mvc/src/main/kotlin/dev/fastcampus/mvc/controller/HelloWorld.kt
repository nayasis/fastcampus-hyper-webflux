package dev.fastcampus.mvc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld {

    @GetMapping("/")
    fun index(): String {
        return "Hello World"
    }
}