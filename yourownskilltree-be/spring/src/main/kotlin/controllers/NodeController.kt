package org.aburavov.yourownskilltree.backend.spring.controllers

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.api.model.*

import org.aburavov.yourownskilltree.backend.api.mappers.*
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.util.decodeJwt
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

private const val AUTH_HEADER = "Authorization"

@Suppress("unused")
@RestController
@RequestMapping("node")
class NodeController (
    private val nodeProcessor: NodeProcessor,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun process (request: IRequest, headers: HttpHeaders): IResponse {
        logger.info { "New request: ${request.requestType}" }
        val principal = decodeJwt(headers[AUTH_HEADER]?.firstOrNull()?:"")
        val ctx = NodeContext()
        ctx.userId = principal.userId
        ctx.userGroup = principal.userGroup
        ctx.fromTransport(request)
        nodeProcessor.process(ctx)
        return ctx.toTransportNode()
    }

    @PostMapping("create")
    suspend fun create(@RequestBody request: NodeCreateRequest, @RequestHeader headers: HttpHeaders): NodeCreateResponse {
        return process(request, headers) as NodeCreateResponse
    }

    @PostMapping("search")
    suspend fun search(@RequestBody request: NodeSearchRequest, @RequestHeader headers: HttpHeaders): NodeSearchResponse {
        return process(request, headers) as NodeSearchResponse
    }

    @PostMapping("read")
    suspend fun read(@RequestBody request: NodeReadRequest, @RequestHeader headers: HttpHeaders): NodeReadResponse {
        return process(request, headers) as NodeReadResponse
    }

    @PostMapping("update")
    suspend fun update(@RequestBody request: NodeUpdateRequest, @RequestHeader headers: HttpHeaders): NodeUpdateResponse {
        return process(request, headers) as NodeUpdateResponse
    }

    @PostMapping("delete")
    suspend fun delete(@RequestBody request: NodeDeleteRequest, @RequestHeader headers: HttpHeaders): NodeDeleteResponse {
        return process(request, headers) as NodeDeleteResponse
    }
}
