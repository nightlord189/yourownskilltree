package org.aburavov.yourownskilltree.backend.repo.mongo

data class MongoConfig(
    var host: String = "localhost",
    var port: Int = 27017,
    var user: String = "",
    var password: String = "",
    var database: String = "master"
)

fun getConnectionString(cfg: MongoConfig):String {
    val sb = StringBuilder()

    // Базовый протокол
    sb.append("mongodb://")

    // Credentials если указаны
    if (cfg.user.isNotEmpty() && cfg.password.isNotEmpty()) {
        sb.append(cfg.user)
        sb.append(":")
        sb.append(cfg.password)
        sb.append("@")
    }

    // Хост и порт
    sb.append(cfg.host)
    sb.append(":")
    sb.append(cfg.port)

    // База данных
    sb.append("/")
    sb.append(cfg.database)

    return sb.toString()
}