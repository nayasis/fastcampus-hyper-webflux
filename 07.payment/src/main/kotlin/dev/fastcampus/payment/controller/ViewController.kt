package dev.fastcampus.payment.controller

import dev.fastcampus.payment.repository.ProductRepository
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

private val logger = KotlinLogging.logger {}

@Controller
class ViewController(
    private val productRepository: ProductRepository
) {

    @RequestMapping("/hello")
    suspend fun index(@RequestParam name: String?, model: Model): String {

        logger.debug { ">> name : $name" }


//        model.addAttribute("name", ReactiveDataDriverContextVariable(name))
        model.addAttribute("name", name)
        val products = productRepository.findAll().toList()
        logger.debug { ">> size : ${products.size}" }
        model.addAttribute("products", products)

        return "hello.html"
    }

}