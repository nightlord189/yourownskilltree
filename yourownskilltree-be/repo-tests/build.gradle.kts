plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.junit)
    implementation(libs.assertj)
    implementation(libs.coroutines.test)

    implementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
