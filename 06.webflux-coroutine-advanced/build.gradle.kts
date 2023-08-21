import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "dev.fastcampus"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	jcenter()
}

dependencies {

	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.7.2")
	implementation("io.micrometer:context-propagation:1.0.3")
	implementation("au.com.console:kassava:2.1.0")

	implementation("org.springframework.boot:spring-boot-starter-aop")

	// circuit breaker
	implementation("io.github.resilience4j:resilience4j-kotlin:2.0.0")
	implementation("io.github.resilience4j:resilience4j-all:2.0.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.mariadb:r2dbc-mariadb")
//	tesruntimeOnly("org.mariadb.jdbc:mariadb-java-client")
//	runtimeOnly("com.h2database:h2")
//	runtimeOnly("io.r2dbc:r2dbc-h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
	testImplementation("io.kotest:kotest-assertions-core:5.6.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
