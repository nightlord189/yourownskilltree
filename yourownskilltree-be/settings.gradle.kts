rootProject.name = "yourownskilltree-be"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    includeBuild("../build-plugin")
    plugins {
        id("build-jvm") apply false
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Включает вот такую конструкцию
//implementation(projects.m2l5Gradle.sub1.ssub1)
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":common")
include(":api")
include(":spring")
include(":rabbit")
include(":biz")
include(":cor")
include(":repo-inmemory")
include(":repo-mongo")