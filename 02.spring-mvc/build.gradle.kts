import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.21"
	kotlin("plugin.spring") version "1.8.21"
	kotlin("plugin.jpa") version "1.8.21"
}

group = "dev.fastcampus"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

//allOpen {
//	annotation("javax.persistence.Entity")
//	annotation("javax.persistence.MappedSuperclass")
//	annotation("javax.persistence.Embeddable")
//}
//
//noArg {
//	annotation("javax.persistence.Entity")
//	annotation("javax.persistence.MappedSuperclass")
//	annotation("javax.persistence.Embeddable")
//	annotation("com.github.nayasis.kotlin.basica.annotation.NoArg")
//	invokeInitializers = true
//}


repositories {
	mavenCentral()
	gradlePluginPortal()
	maven {
		url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
	}
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
//	runtimeOnly("com.mysql:mysql-connector-j")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
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