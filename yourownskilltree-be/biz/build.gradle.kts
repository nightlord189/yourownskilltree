plugins {
    alias(libs.plugins.kotlinx.serialization)
    id("build-jvm")
}

dependencies {
    implementation(libs.kotlin.logging)

    implementation(project(":api"))
    implementation(project(":common"))
}


repositories {
    mavenCentral()
}
