package org.aburavov.yourownskilltree.backend.common.model

class NodeContext: RequestContext() {
    var command: NodeCommand = NodeCommand.NONE

    var nodeRequest: Node? = null
    var nodeFilterRequest: NodeFilter? = null

    var nodeResponse: Node? = null
    var nodesResponse: MutableList<Node>? = null
}

enum class NodeCommand {
    NONE,
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SEARCH,
}
