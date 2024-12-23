package org.aburavov.yourownskilltree.backend.common.model

open class Node {
    var id: String = ""
    var name: String = ""
    var description: String? = null
    var completionType: NodeCompletionType = NodeCompletionType.BOOL
    var status: NodeStatus = NodeStatus.OPEN
    var parentIds: List<String> = emptyList()
    var progress: Int? = null
    var questions: List<Question>? = null
    var lock: String = ""
}

enum class NodeCompletionType {
    BOOL,
    PERCENTAGE,
    TEST,
}

enum class NodeStatus {
    CLOSED,
    OPEN,
    IN_PROGRESS,
    COMPLETED;
}