package org.aburavov.yourownskilltree.backend.common.model

class NodeContext: RequestContext() {
    var command: NodeCommand = NodeCommand.NONE

    var nodeRequest: Node? = null
    var nodeFilterRequest: NodeFilter? = null
    var nodeIdRequest: String? = null
    var nodeLock: String? = null // for delete case

    var nodeResponse: Node? = null
    var nodesResponse: List<Node>? = null

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
