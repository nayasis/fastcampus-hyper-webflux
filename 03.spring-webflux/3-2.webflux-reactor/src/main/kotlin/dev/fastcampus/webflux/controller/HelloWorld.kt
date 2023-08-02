package dev.fastcampus.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloWorld {

    @GetMapping("/")
    fun index(): Mono<String> {
        return Mono.just("Hello webflux")
    }

}