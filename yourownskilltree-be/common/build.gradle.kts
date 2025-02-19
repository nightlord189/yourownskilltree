plugins {
    id("build-jvm")
    kotlin("jvm") version "1.9.23"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.coroutines.test)

    testImplementation(kotlin("test"))
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
