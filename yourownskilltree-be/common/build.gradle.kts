plugins {
    id("build-jvm")
    kotlin("jvm") version "1.9.23"
    application
}

application {
    mainClass.set("org.aburavov.yourownskilltree.backend.common.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.aburavov.yourownskilltree.backend.common.MainKt"
    }
}