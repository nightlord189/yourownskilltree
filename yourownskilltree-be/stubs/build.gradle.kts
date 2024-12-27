plugins {
    id("build-jvm")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":cor"))
}

tasks.test {
    useJUnitPlatform()
}
