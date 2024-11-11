package org.aburavov.yourownskilltree.backend.common.model

class NodeContext: RequestContext() {
    var command: NodeCommand = NodeCommand.NONE

    var nodeRequest: Node = Node()
    var nodeFilterRequest: NodeFilter = NodeFilter()

    var nodeResponse: Node = Node()
    var nodesResponse: MutableList<Node> = mutableListOf()
}

enum class NodeCommand {
    NONE,
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SEARCH,
}
