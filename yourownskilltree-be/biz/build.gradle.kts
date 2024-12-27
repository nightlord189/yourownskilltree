plugins {
    id("build-jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.logging)
    implementation(libs.coroutines.core)

    implementation(project(":common"))
    implementation(project(":cor"))
    implementation(project(":stubs"))

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}
