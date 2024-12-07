plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.squareup.moshi:moshi-adapters:1.15.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
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