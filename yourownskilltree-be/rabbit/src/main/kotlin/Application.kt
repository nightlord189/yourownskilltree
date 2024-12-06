package org.aburavov.yourownskilltree.rabbit

import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.rabbit.config.loadConfig
import org.aburavov.yourownskilltree.rabbit.controllers.RabbitController

fun main() {
    println("Start Rabbit")

    val cfg = loadConfig()

    val rabbitController = RabbitController(cfg)
    rabbitController.startProcessing()
}