plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.rabbitmq.client)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)

    implementation(libs.jackson.kotlin)

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":biz"))

    testImplementation(kotlin("test"))
    testImplementation(libs.testcontainers.rabbitmq)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}