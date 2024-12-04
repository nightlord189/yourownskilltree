plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.swagger.annotations)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.okhttp)

    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsr310)

    testImplementation(libs.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.jackson.kotlin)
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("${rootProject.projectDir}/../docs/openapi/api.yml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("com.yourownskilltree.backend.api")
    modelPackage.set("com.yourownskilltree.backend.api.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "enumPropertyNaming" to "UPPERCASE",
            "serializationLibrary" to "jackson",
            "collectionType" to "list"
        )
    )
    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false"
        )
    )
}

sourceSets {
    main {
        java {
            srcDir("${buildDir}/generated/src/main/kotlin")
        }
    }
}

tasks {
    compileKotlin {
        dependsOn("openApiGenerate")
    }
    test {
        useJUnitPlatform()
    }
}