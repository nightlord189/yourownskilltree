package org.aburavov.yourownskilltree.backend.spring.controllers

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.api.model.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import org.aburavov.yourownskilltree.backend.api.model.*
import org.aburavov.yourownskilltree.backend.api.mappers.*
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.spring.services.NodeService

@Suppress("unused")
@RestController
@RequestMapping("node")
class NodeController (
    private val nodeService: NodeService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("create")
    suspend fun create(@RequestBody request: NodeCreateRequest): NodeCreateResponse {
        logger.info{"create request"}
        val ctx = NodeContext()
        ctx.fromTransport(request)
        nodeService.process(ctx)
        return ctx.toTransportCreate()
    }

    @PostMapping("search")
    suspend fun search(@RequestBody request: NodeSearchRequest): NodeSearchResponse {
        logger.info{"search request"}
        return NodeSearchResponse()
    }

    @PostMapping("read")
    suspend fun read(@RequestBody request: NodeReadRequest): NodeReadResponse {
        logger.info{"read request"}
        return NodeReadResponse()
    }

    @PostMapping("update")
    suspend fun update(@RequestBody request: NodeUpdateRequest): NodeUpdateResponse {
        logger.info{"update request"}
        return NodeUpdateResponse()
    }

    @PostMapping("delete")
    suspend fun delete(@RequestBody request: NodeDeleteRequest): NodeDeleteResponse {
        logger.info{"delete request"}
        return NodeDeleteResponse()
    }
}