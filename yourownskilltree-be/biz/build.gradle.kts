plugins {
    id("build-jvm")
}

dependencies {
    implementation(libs.kotlin.logging)

    implementation(project(":common"))
    implementation(project(":cor"))
}


repositories {
    mavenCentral()
}
