package dev.fastcampus.coroutine

import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Hooks

@SpringBootApplication
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class Application

fun main(args: Array<String>) {

	System.setProperty("java.net.preferIPv4Stack", "true")

	Hooks.enableAutomaticContextPropagation()
	ContextRegistry.getInstance().registerThreadLocalAccessor(
		"txid",
		{ MDC.get("txid") },
		{ value -> MDC.put("txid", value) },
		{ MDC.remove("txid") }
	)

	runApplication<Application>(*args)

}