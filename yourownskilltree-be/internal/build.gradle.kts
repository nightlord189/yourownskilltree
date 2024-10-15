plugins {
    id("build-jvm")
    kotlin("jvm") version "1.9.23"
    application
}

application {
    // Указать главный класс
    mainClass.set("org.aburavov.yourownskilltree.backend.internal.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.aburavov.yourownskilltree.backend.internal.MainKt"
    }
}
