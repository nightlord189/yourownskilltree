package org.aburavov.yourownskilltree.backend.spring.controllers

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.api.model.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import org.aburavov.yourownskilltree.backend.api.model.*
import org.aburavov.yourownskilltree.backend.api.mappers.*
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.NodeContext

@Suppress("unused")
@RestController
@RequestMapping("node")
class NodeController (
    private val nodeProcessor: NodeProcessor,
) {
    private val logger = KotlinLogging.logger {}

    fun process (request: IRequest): IResponse {
        logger.info { "New request: ${request.requestType}" }
        val ctx = NodeContext()
        ctx.fromTransport(request)
        nodeProcessor.process(ctx)
        return ctx.toTransportNode()
    }

    @PostMapping("create")
    suspend fun create(@RequestBody request: NodeCreateRequest): NodeCreateResponse {
        return process(request) as NodeCreateResponse
    }

    @PostMapping("search")
    suspend fun search(@RequestBody request: NodeSearchRequest): NodeSearchResponse {
        return process(request) as NodeSearchResponse
    }

    @PostMapping("read")
    suspend fun read(@RequestBody request: NodeReadRequest): NodeReadResponse {
        return process(request) as NodeReadResponse
    }

    @PostMapping("update")
    suspend fun update(@RequestBody request: NodeUpdateRequest): NodeUpdateResponse {
        return process(request) as NodeUpdateResponse
    }

    @PostMapping("delete")
    suspend fun delete(@RequestBody request: NodeDeleteRequest): NodeDeleteResponse {
        return process(request) as NodeDeleteResponse
    }
}