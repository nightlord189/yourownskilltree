plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    testImplementation(project(":repo-tests"))

    implementation(libs.kotlin.logging)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
