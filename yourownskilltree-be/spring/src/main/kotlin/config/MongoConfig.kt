package org.aburavov.yourownskilltree.backend.spring.config

import org.aburavov.yourownskilltree.backend.repo.mongo.MongoConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("mongo")
data class MongoConfig (
    val host: String = "localhost",
    val port: Int = 27017,
    val user: String = "",
    val password: String = "",
    val database: String = "master"
) {
    fun toRepoConfig(): MongoConfig {
        return MongoConfig(
            host = this.host,
            port = this.port,
            user = this.user,
            password = this.password,
            database = this.database
        )
    }
}