package dev.fastcampus.payment

import dev.fastcampus.payment.model.Product
import dev.fastcampus.payment.repository.ProductRepository
import io.micrometer.context.ContextRegistry
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Hooks

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableR2dbcAuditing
@EnableR2dbcRepositories
class PaymentApplication

fun main(args: Array<String>) {

	System.setProperty("java.net.preferIPv4Stack", "true")

	Hooks.enableAutomaticContextPropagation()
	ContextRegistry.getInstance().registerThreadLocalAccessor(
		"txid",
		{ MDC.get("txid") },
		{ value -> MDC.put("txid", value) },
		{ MDC.remove("txid") }
	)

	runApplication<PaymentApplication>(*args)
}

@Configuration
@ConditionalOnProperty(name=["spring.profiles.active"], havingValue="local" )
class TestDataInitializer(
	private val productRepository: ProductRepository,
): ApplicationListener<ApplicationReadyEvent> {
	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		runBlocking {
			listOf(
				Product(  1, "apple", "사과", 120 ),
				Product(  2, "strawberry", "딸기", 150 ),
				Product(  3, "orange", "오렌지", 100 ),
			).forEach { prod ->
				if(! productRepository.existsById(prod.id)) {
					prod.new = true
				}
				productRepository.save(prod)
			}
		}
		logger.info { ">> Data initialized" }
	}
}