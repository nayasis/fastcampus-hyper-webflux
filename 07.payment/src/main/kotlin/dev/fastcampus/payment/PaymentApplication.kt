package dev.fastcampus.payment

import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Hooks
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

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