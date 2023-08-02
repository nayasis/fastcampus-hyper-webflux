import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import org.graalvm.buildtools.gradle.tasks.BuildNativeImageTask
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.21"
	kotlin("plugin.spring") version "1.8.21"
	kotlin("plugin.jpa") version "1.8.21"
//	id("org.graalvm.buildtools.native") version "0.9.23"
}

group = "dev.study.mvc"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	gradlePluginPortal()
	maven {
		url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
	}
}

dependencies {

	implementation("io.github.microutils:kotlin-logging:3.0.5")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

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

//tasks.withType<BuildNativeImageTask>().configureEach {
//	disableToolchainDetection.set(false)
//}

//graalvmNative {
//	binaries.all {
//		resources.autodetect()
//	}
//	toolchainDetection.set(false)
//}

tasks.withType<BootBuildImage> {
	// set tls cert pipeline for company's security policy
	docker {
		tlsVerify.set(false)
	}
//	bindings.add("${project.projectDir}/ca-certficates/binding:/bindings/ca-certificates")
//	environment.put("BPE_APPEND_JAVA_TOOL_OPTIONS", "\"-Xmx2048m -XX:MaxDirectMemorySize=1G")
	environment.put("BPE_APPEND_JAVA_TOOL_OPTIONS", "\"-Xmx4096m")
	environment.put("BPL_JVM_HEAD_ROOM", "5")
}