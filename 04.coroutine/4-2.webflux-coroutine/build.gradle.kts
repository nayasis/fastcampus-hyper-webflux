import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "dev.fastcampus"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	runtimeOnly("org.mariadb:r2dbc-mariadb")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("io.kotest:kotest-runner-junit5:5.6.1")
	testImplementation("io.kotest:kotest-assertions-core:5.6.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

	testImplementation("org.jetbrains.exposed:exposed:0.17.14")
	testImplementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

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