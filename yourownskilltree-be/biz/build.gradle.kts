plugins {
    id("build-jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.logging)

    implementation(project(":common"))
    implementation(project(":cor"))

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}
