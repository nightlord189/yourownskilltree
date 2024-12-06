package org.aburavov.yourownskilltree.rabbit.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.api.mappers.fromTransport
import org.aburavov.yourownskilltree.backend.api.mappers.toTransportNode
import org.aburavov.yourownskilltree.backend.api.model.IRequest
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.rabbit.config.RabbitConfig

class RabbitController(val config: RabbitConfig) {
    private val factory = ConnectionFactory().apply {
        host = config.host
        port = config.port
        username = config.username
        password = config.password
    }

    private val logger = KotlinLogging.logger {}

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    val nodeProcessor = NodeProcessor()

    private val connection = factory.newConnection()
    private val channel = connection.createChannel()

    init {
        channel.queueDeclare(config.inputQueue, false, false, false, null)
        channel.queueDeclare(config.outputQueue, false, false, false, null)
    }

    fun startProcessing() {
        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray
            ) {
                val request = objectMapper.readValue(body, object : TypeReference<IRequest>() {})
                logger.info {"Received: $request"}

                val ctx = NodeContext()
                ctx.fromTransport(request)
                nodeProcessor.process(ctx)

                logger.info { "Processed ctx: $ctx" }

                val outputMsg = ctx.toTransportNode()

                logger.info {"OutputMsg: $outputMsg"}

                channel.basicPublish(
                    "",
                    config.outputQueue,
                    null,
                    objectMapper.writeValueAsString(outputMsg).toByteArray(),
                )

                logger.info {"Sent to output queue: $outputMsg"}
                channel.basicAck(envelope.deliveryTag, false)
            }
        }

        logger.info { "RabbitController: start consuming" }
        channel.basicConsume(config.inputQueue, false, consumer)
    }

    fun close() {
        channel.close()
        connection.close()
    }
}