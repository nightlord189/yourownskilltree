package org.aburavov.yourownskilltree.rabbit.config

data class RabbitConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val inputQueue: String,
    val outputQueue: String,
)

fun loadConfig (): RabbitConfig {
    return RabbitConfig(
        host = System.getenv("RABBIT_HOST") ?: "localhost",
        port = System.getenv("RABBIT_PORT")?.toIntOrNull() ?: 5672,
        username = System.getenv("RABBIT_USER") ?: "guest",
        password = System.getenv("RABBIT_PASSWORD") ?: "guest",
        inputQueue = System.getenv("RABBIT_INPUT_QUEUE") ?: "input-queue",
        outputQueue = System.getenv("RABBIT_OUTPUT_QUEUE") ?: "output-queue",
    )
}