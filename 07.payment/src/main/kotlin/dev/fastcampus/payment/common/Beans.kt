package dev.fastcampus.payment.common

import dev.fastcampus.payment.repository.ProductRepository
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Beans: ApplicationContextAware {

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    companion object {

        lateinit var context: ApplicationContext
            private set

        fun <T: Any> getBean(byClass: KClass<T>, vararg arg: String): T {
            return context.getBean(byClass.java, *arg)
        }

        val productRepository: ProductRepository by lazy { getBean(ProductRepository::class) }

    }


}