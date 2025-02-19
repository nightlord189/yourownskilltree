package org.aburavov.yourownskilltree.backend.biz.auth

import org.aburavov.yourownskilltree.backend.common.model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeCommand
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import org.aburavov.yourownskilltree.backend.common.permissions.Permission
import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup
import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import org.aburavov.yourownskilltree.backend.cor.Worker

val fullPermissions = setOf(Permission.READ, Permission.READ_FULL, Permission.CREATE, Permission.UPDATE, Permission.DELETE);

class CheckIsAuthorized() : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        return ctx.userId != null && ctx.userGroup != UserGroup.GUEST
    }
}

class CheckPermissions(
    private val accessEntityRepo: IRepoAccessEntity
) : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        val res = checkCommand(ctx)
        if (!res) {
            ctx.addError("access denied")
        }
        return res
    }

    private suspend fun checkCommand (ctx: NodeContext): Boolean {
        when (ctx.command) {
            NodeCommand.NONE -> return true
            NodeCommand.CREATE -> return ctx.permissions.contains(Permission.CREATE)
            NodeCommand.READ -> return ctx.permissions.contains(Permission.READ) || ctx.permissions.contains(Permission.READ_FULL)
            NodeCommand.UPDATE -> return ctx.permissions.contains(Permission.UPDATE)
            NodeCommand.DELETE -> return ctx.permissions.contains(Permission.DELETE)
            NodeCommand.SEARCH ->  {
                val iterator = ctx.nodesResponse?.iterator()
                while (iterator?.hasNext() == true) {
                    val node = iterator.next()
                    val accessResp = accessEntityRepo.read(ctx.userId ?: "", node.id)
                    val permissions = getPermissionsToSingleNode(ctx.userId, ctx.userGroup, node, accessResp.data)
                    if (!(permissions.contains(Permission.READ) || permissions.contains(Permission.READ_FULL))) {
                        iterator.remove()  // удаляем элемент, если у пользователя нет прав на чтение
                    } else {
                        if (!permissions.contains(Permission.READ_FULL)) {
                            node.cleanSensitiveData()
                        }
                    }
                }
                return true
            }
        }
    }
}

class CalculatePermissions(
    private val accessEntityRepo: IRepoAccessEntity
) : Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext) = true

    override suspend fun handle(ctx: NodeContext): Boolean {
        when (ctx.command) {
            NodeCommand.NONE, NodeCommand.SEARCH -> ctx.permissions = emptySet() // у SEARCH своя кастомная проверка на каждую ноду

            NodeCommand.CREATE -> {
                ctx.permissions = getPermissionsToSingleNode(ctx.userId, ctx.userGroup, null, null)
            }

            NodeCommand.READ, NodeCommand.DELETE -> {
                val accessResp = accessEntityRepo.read(ctx.userId ?: "", ctx.nodeIdRequest?:"")
                ctx.permissions = getPermissionsToSingleNode(ctx.userId, ctx.userGroup, ctx.nodeResponse, accessResp.data)
            }

            NodeCommand.UPDATE -> {
                val accessResp = accessEntityRepo.read(ctx.userId ?: "", ctx.nodeRequest?.id?:"")
                ctx.permissions = getPermissionsToSingleNode(ctx.userId, ctx.userGroup, ctx.nodeResponse, accessResp.data)
            }
        }
        return true
    }
}


public suspend fun getPermissionsToSingleNode (userId: String?, userGroup: UserGroup, node: Node?, accessEntity: AccessEntity?): Set<Permission> {
    // админ может делать все
    if (userGroup == UserGroup.ADMIN) {
        return fullPermissions
    }

    // owner тоже может делать все
    if (userId == node?.ownerId) {
        return fullPermissions
    }

    // по дефолту
    val permissionsSet = HashSet<Permission>()

    // создавать могут все кроме гостей
    if (userGroup != UserGroup.GUEST) {
        permissionsSet.add(Permission.CREATE)
    }

    // есть разрешение в базе
    if (accessEntity != null) {
        when (accessEntity.accessLevel) {
            NodeAccessLevel.FULL_ACCESS -> permissionsSet.addAll(fullPermissions)
            NodeAccessLevel.FULL_READ -> {
                permissionsSet.add(Permission.READ)
                permissionsSet.add(Permission.READ_FULL)
            }
            NodeAccessLevel.GENERAL_READ -> permissionsSet.add(Permission.READ)
        }
    }

    // если ноду расшарили публично на всех
    when (node?.publicAccessLevel) {
        NodeAccessLevel.FULL_ACCESS -> {
            if (userGroup == UserGroup.GUEST) {
                permissionsSet.add(Permission.READ) // если гость, то все равно только обычное чтение
            } else {
                permissionsSet.addAll(fullPermissions)
            }
        }
        NodeAccessLevel.FULL_READ -> {
            permissionsSet.add(Permission.READ)

            if (userGroup != UserGroup.GUEST) {
                permissionsSet.add(Permission.READ_FULL) // полное чтение только авторизованному юзеру
            }
        }
        NodeAccessLevel.GENERAL_READ -> permissionsSet.add(Permission.READ)
        null -> {}
    }

    return permissionsSet
}
