package org.aburavov.yourownskilltree.backend.common.model

open class Node {
    var name: String = ""
    var completionType: NodeCompletionType = NodeCompletionType.BOOL
    var status: NodeStatus = NodeStatus.CLOSED
    var description: String? = null
    var parentIds: List<String> = emptyList()
    var progress: Int? = null
    var questions: List<Question>? = null
}

class NodeResponse (
    val id: String,
): Node ()

enum class NodeCompletionType (var value: String) {
    BOOL ("bool"),
    PERCENTAGE ("percentage"),
    TEST ("test"),
}

enum class NodeStatus(val value: String) {
    CLOSED("closed"),
    OPEN("open"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");
}