package org.aburavov.yourownskilltree.backend.biz.auth

import org.aburavov.yourownskilltree.backend.common.model.NodeCommand
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.cor.Worker
import permissions.NodeAccessLevel
import permissions.UserGroups
import repo.IRepoAccessEntity
import repo.IRepoNode

class CheckPermissions(
    private val nodeRepo: IRepoNode,
    private val accessEntityRepo: IRepoAccessEntity
) : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        when (ctx.command) {
            NodeCommand.NONE -> return true

            NodeCommand.CREATE -> {
                return ctx.userId != null && ctx.userGroup != UserGroups.GUEST
            }

            NodeCommand.READ, NodeCommand.UPDATE, NodeCommand.DELETE -> {
                // админ может делать все
                if (ctx.userGroup == UserGroups.ADMIN) {
                    return true
                }

                // owner тоже может делать все
                if (ctx.userId == ctx.nodeResponse?.ownerId) {
                    return true
                }

                // сущность расшарили публично на всех
                if (ctx.nodeResponse?.isPublic == true) {
                    if (ctx.command == NodeCommand.UPDATE || ctx.command == NodeCommand.DELETE) {
                        return ctx.nodeResponse?.publicAccessLevel == NodeAccessLevel.FULL_ACCESS
                    } else if (ctx.command == NodeCommand.READ) {
                        return true
                    }
                }

                // проверяем наличие конкретного разрешения
                val resourceId = ctx.nodeResponse?.id
                if (ctx.userId == null || resourceId == null) {
                    return false
                }
                val access = accessEntityRepo.read(ctx.userId ?: "", resourceId)
                if (access.errors.firstOrNull()?.message == "not found") {
                    return false
                }
                if (access.errors.isNotEmpty()) {
                    ctx.errors.addAll(access.errors)
                    return false
                }
                if (ctx.command == NodeCommand.UPDATE || ctx.command == NodeCommand.DELETE) {
                    return access.data?.accessLevel == NodeAccessLevel.FULL_ACCESS
                } else if (ctx.command == NodeCommand.READ) {
                    return true
                }
            }

            NodeCommand.SEARCH -> {

            }
        }
        return true
    }

    private fun getAccessToSingleNode (ctx: NodeContext): NodeAccessLevel {
        // админ может делать все
        if (ctx.userGroup == UserGroups.ADMIN) {
            return NodeAccessLevel.FULL_ACCESS
        }

        // owner тоже может делать все
        if (ctx.userId == ctx.nodeResponse?.ownerId) {
            return true
        }

        // сущность расшарили публично на всех
        if (ctx.nodeResponse?.isPublic == true) {
            if (ctx.command == NodeCommand.UPDATE || ctx.command == NodeCommand.DELETE) {
                return ctx.nodeResponse?.publicAccessLevel == NodeAccessLevel.FULL_ACCESS
            } else if (ctx.command == NodeCommand.READ) {
                return true
            }
        }

        // проверяем наличие конкретного разрешения
        val resourceId = ctx.nodeResponse?.id
        if (ctx.userId == null || resourceId == null) {
            return false
        }
        val access = accessEntityRepo.read(ctx.userId ?: "", resourceId)
        if (access.errors.firstOrNull()?.message == "not found") {
            return false
        }
        if (access.errors.isNotEmpty()) {
            ctx.errors.addAll(access.errors)
            return false
        }
        if (ctx.command == NodeCommand.UPDATE || ctx.command == NodeCommand.DELETE) {
            return access.data?.accessLevel == NodeAccessLevel.FULL_ACCESS
        } else if (ctx.command == NodeCommand.READ) {
            return true
        }
    }
}

class CheckIsAuthorized() : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        return ctx.userId != null && ctx.userGroup != UserGroups.GUEST
    }
}
