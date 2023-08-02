plugins {
    kotlin("jvm") version "1.8.21"
}

group = "dev.fastcampus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.projectreactor:reactor-core:3.5.7")

//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
//    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(8)
}