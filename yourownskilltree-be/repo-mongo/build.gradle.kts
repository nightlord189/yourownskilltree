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

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
