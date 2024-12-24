plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.logging)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}
