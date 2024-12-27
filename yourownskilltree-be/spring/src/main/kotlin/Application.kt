package org.aburavov.yourownskilltree.backend.spring

import org.aburavov.yourownskilltree.backend.spring.config.MongoConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationProperties
@EnableConfigurationProperties(MongoConfig::class)
class Application

// swagger URL: http://localhost:8080/swagger-ui.html

fun main(args: Array<String>) {
    println("Start Spring")
    runApplication<Application>(*args)
}