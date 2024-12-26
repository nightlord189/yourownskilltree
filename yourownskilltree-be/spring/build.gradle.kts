plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencies)
    alias(libs.plugins.spring.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    id("build-jvm")
}

dependencies {
    implementation(libs.spring.actuator)
    implementation(libs.spring.webflux)
    implementation(libs.spring.webflux.ui)
    implementation(libs.jackson.kotlin)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.reactor)

    implementation(libs.kotlin.logging)
    implementation(libs.logback)

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":biz"))
    implementation(project(":repo-mongo"))
    implementation(project(":repo-inmemory"))
    implementation(project(":stubs"))

    testImplementation(libs.spring.test)
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}
