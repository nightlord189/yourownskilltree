package org.aburavov.yourownskilltree.backend.biz

import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.cor.Worker

class ChainErrorsStopper(): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return true
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        if (ctx.errors.isEmpty()) {
           return true
        }
        ctx.nodesResponse = null
        ctx.nodeResponse = null
        return false
    }
}
