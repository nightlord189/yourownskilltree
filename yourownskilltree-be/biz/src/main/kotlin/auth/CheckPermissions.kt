package org.aburavov.yourownskilltree.backend.biz.auth

import org.aburavov.yourownskilltree.backend.common.model.NodeCommand
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.cor.Worker
import permissions.UserGroups
import repo.IRepoNode

class CheckPermissions(
    repo: IRepoNode
) : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        when (ctx.command) {
            NodeCommand.NONE -> return true
            NodeCommand.CREATE -> {
                return ctx.userId != null && ctx.userGroup != UserGroups.GUEST
            }
            NodeCommand.UPDATE, NodeCommand.DELETE -> {
                if (ctx.userId == ctx.nodeResponse?.ownerId) {
                    return true
                }
                // TODO: read permission from db
            }

            NodeCommand.READ -> TODO()
            NodeCommand.SEARCH -> TODO()
        }
        return true
    }
}

class CheckIsAuthorized() : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        return ctx.userId != null && ctx.userGroup != UserGroups.GUEST
    }
}
