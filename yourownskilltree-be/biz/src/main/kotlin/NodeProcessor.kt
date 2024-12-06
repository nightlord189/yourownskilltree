package org.aburavov.yourownskilltree.backend.biz

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.api.mappers.*
import org.aburavov.yourownskilltree.backend.common.model.*
import java.util.*

val unsupportedStubsError = CommonError(message="unsupported stubs")

class NodeProcessor {
    val nodes: MutableList<Node> = mutableListOf() //in-memory list for dummy responses
    private val logger = KotlinLogging.logger {}

    fun process(ctx: NodeContext) {
        logger.info { "processing NodeContext with workMode ${ctx.workMode} and command ${ctx.command}" }
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
            NodeCommand.UPDATE -> {
                when (ctx.stubCase) {
                    NodeStubs.NONE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.SUCCESS -> {
                        logger.info { "updating node: ${ctx.nodeRequest}, all nodes: $nodes" }
                        val index = nodes.indexOfFirst { it.id == ctx.nodeRequest?.id }
                        if (index != -1) {
                            nodes[index] = ctx.nodeRequest!!
                            ctx.nodeResponse = nodes[index]
                        } else {
                            logger.error { "cannot find node with id ${ctx.nodeRequest?.id} to update" }
                        }
                    }
                    NodeStubs.NOT_FOUND -> ctx.errors.add(CommonError(message="not found"))
                    NodeStubs.BAD_ID -> ctx.errors.add(CommonError(message="bad id"))
                    NodeStubs.CANNOT_DELETE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.DB_ERROR -> ctx.errors.add(CommonError(message="db error"))
                }
            }
            NodeCommand.DELETE -> {
                when (ctx.stubCase) {
                    NodeStubs.NONE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.SUCCESS -> {
                        nodes.removeIf {it.id == ctx.nodeRequest?.id}
                    }
                    NodeStubs.NOT_FOUND -> ctx.errors.add(CommonError(message="not found"))
                    NodeStubs.BAD_ID -> ctx.errors.add(CommonError(message="bad id"))
                    NodeStubs.CANNOT_DELETE -> ctx.errors.add(CommonError(message="cannot delete"))
                    NodeStubs.DB_ERROR -> ctx.errors.add(CommonError(message="db error"))
                }
            }
            NodeCommand.SEARCH -> {
                when (ctx.stubCase) {
                    NodeStubs.NONE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.SUCCESS -> {
                        ctx.nodesResponse = nodes.
                        filter { (ctx.nodeFilterRequest?.nameLike ?: "") in it.name}.
                        filter { node ->
                            ctx.nodeFilterRequest?.parentId?.let { parentId ->
                                node.parentIds.contains(parentId)
                            } ?: true
                        }
                            .toMutableList()
                    }
                    NodeStubs.NOT_FOUND -> ctx.errors.add(CommonError(message="not found"))
                    NodeStubs.BAD_ID -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.CANNOT_DELETE -> ctx.errors.add(unsupportedStubsError)
                    NodeStubs.DB_ERROR -> ctx.errors.add(CommonError(message="db error"))
                }
            }
            NodeCommand.NONE -> throw Exception("unknown command")
        }
    }
}