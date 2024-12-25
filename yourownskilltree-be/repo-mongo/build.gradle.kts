plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.mongodb.driver.coroutine)

    implementation(libs.kotlin.logging)
    implementation(libs.logback)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.coroutines.test)

    testImplementation(kotlin("test"))
    testImplementation(libs.testcontainers.mongodb)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
}

tasks.test {
    useJUnitPlatform()
}
