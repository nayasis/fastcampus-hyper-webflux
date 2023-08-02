package dev.fastcampus.coroutine.controller

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {  }

@Aspect
@Component
class DemoAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    fun log(pjp: ProceedingJoinPoint): Any? {
        logger.debug { ">>>>>>> before" }
        return try {
            pjp.proceed()
        } finally {
            logger.info(">>>>>>>>> method: ${pjp.signature} args: ${pjp.args}}")
        }
    }

}