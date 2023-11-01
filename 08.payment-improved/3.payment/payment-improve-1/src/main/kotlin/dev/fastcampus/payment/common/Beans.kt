package dev.fastcampus.payment.common

import dev.fastcampus.payment.repository.ProductInOrderRepository
import dev.fastcampus.payment.service.OrderService
import dev.fastcampus.payment.service.ProductService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Beans: ApplicationContextAware {

    companion object {
        lateinit var ctx : ApplicationContext
            private set

        fun <T: Any> getBean(byClass: KClass<T>, vararg arg: Any): T {
            return ctx.getBean(byClass.java, arg)
        }

        val beanProductInOrderRepository: ProductInOrderRepository by lazy{ getBean(ProductInOrderRepository::class) }
        val beanProductService: ProductService by lazy{ getBean(ProductService::class) }
        val beanOrderService: OrderService by lazy{ getBean(OrderService::class) }

    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }
}