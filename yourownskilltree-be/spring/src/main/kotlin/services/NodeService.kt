package org.aburavov.yourownskilltree.backend.spring.services

import org.aburavov.yourownskilltree.backend.api.mappers.*
import org.aburavov.yourownskilltree.backend.common.model.*
import org.springframework.stereotype.Service
import java.util.*

val unsupportedStubsError = CommonError(message="unsupported stubs")

@Service
class NodeService {
    val nodes: MutableList<Node> = mutableListOf()

    fun process(ctx: NodeContext) {
        when (ctx.workMode) {
            WorkMode.PROD, WorkMode.TEST -> {
                ctx.errors.add(CommonError(message="unsupported workMode"))
                return
            }
            WorkMode.STUB -> {}
        }

        when (ctx.command) {
            NodeCommand.CREATE -> {
                when (ctx.stubCase) {
                    NodeStubs.NONE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.SUCCESS -> {
                        ctx.nodeRequest?.id = UUID.randomUUID().toString()
                        nodes.add(ctx.nodeRequest!!)
                        ctx.nodeResponse = ctx.nodeRequest
                    }
                    NodeStubs.NOT_FOUND -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.BAD_ID -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.CANNOT_DELETE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.DB_ERROR -> ctx.errors.add(CommonError(message="db error"))
                }
            }
            NodeCommand.READ -> {
                when (ctx.stubCase) {
                    NodeStubs.NONE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.SUCCESS -> {
                        ctx.nodeResponse = nodes.first { it.id == ctx.nodeRequest?.id }
                    }
                    NodeStubs.NOT_FOUND -> ctx.errors.add(CommonError(message="not found"))
                    NodeStubs.BAD_ID -> ctx.errors.add(CommonError(message="bad id"))
                    NodeStubs.CANNOT_DELETE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.DB_ERROR -> ctx.errors.add(CommonError(message="db error"))
                }
            }
            NodeCommand.UPDATE -> TODO()
            NodeCommand.DELETE -> TODO()
            NodeCommand.SEARCH -> TODO()
            NodeCommand.NONE -> throw Exception("unknown command")
        }
    }
}