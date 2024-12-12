plugins {
    id("build-jvm")
}

dependencies {
    implementation(libs.kotlin.logging)

    implementation(project(":common"))
}


repositories {
    mavenCentral()
}
