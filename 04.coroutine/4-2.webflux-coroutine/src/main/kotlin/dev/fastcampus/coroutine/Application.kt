package dev.fastcampus.coroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class Application

fun main(args: Array<String>) {

	println(">> available process: ${Runtime.getRuntime().availableProcessors()}")

	System.setProperty("reactor.netty.ioWorkerCount", "500")

	runApplication<Application>(*args)

}