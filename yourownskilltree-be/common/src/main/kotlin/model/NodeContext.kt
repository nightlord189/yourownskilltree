package org.aburavov.yourownskilltree.backend.common.model

import org.aburavov.yourownskilltree.backend.common.permissions.Permission
import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup

class NodeContext: RequestContext() {
    var command: NodeCommand = NodeCommand.NONE

    var userId: String? = null
    var userGroup: UserGroup = UserGroup.GUEST
    var permissions: Set<Permission> = emptySet()

    var nodeRequest: Node? = null
    var nodeFilterRequest: NodeFilter? = null
    var nodeIdRequest: String? = null
    var nodeLock: String? = null // for delete case

    var nodeResponse: Node? = null
    var nodesResponse: MutableList<Node>? = null

    fun addError(msg: String) {
        errors.add(CommonError().apply { message = msg })
    }
}

enum class NodeCommand {
    NONE,
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SEARCH,
}
