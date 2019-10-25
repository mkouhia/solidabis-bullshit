import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    java
    application
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"
}

group = "fi.mkouhia.solidabis"
version = "0.2.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlin_version))
    implementation("io.ktor", "ktor-server-netty", ktor_version)
    implementation("ch.qos.logback", "logback-classic", logback_version)
    implementation("io.ktor", "ktor-client-core", ktor_version)
    implementation("io.ktor", "ktor-client-core-jvm", ktor_version)
    implementation("io.ktor", "ktor-client-jetty", ktor_version)
    implementation("io.ktor", "ktor-client-json-jvm", ktor_version)
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktor_version")
    implementation("io.ktor", "ktor-server-core", ktor_version)
    implementation("io.ktor", "ktor-thymeleaf", ktor_version)
    testImplementation("io.ktor", "ktor-server-tests", ktor_version)
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}



tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

// Heroku deployment with shadowJar
tasks.register("stage") {
    dependsOn("clean", "shadowJar")
}

// Do not include version to shadowJar - no need to edit Procfile when deploying to Heroku
tasks.shadowJar {
    archiveVersion.set("")
}
