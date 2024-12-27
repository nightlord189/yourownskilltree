plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.logging)
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
}

tasks.test {
    useJUnitPlatform()
}
