package org.aburavov.yourownskilltree.backend.common.model

import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel

open class Node {
    var id: String = ""
    var name: String = ""
    var description: String? = null
    var completionType: NodeCompletionType = NodeCompletionType.BOOL
    var status: NodeStatus = NodeStatus.OPEN
    var parentIds: List<String> = emptyList()
    var progress: Int? = null
    var questions: List<Question>? = null
    var ownerId: String = ""
    var publicAccessLevel: NodeAccessLevel? = null  // уровень публичного доступа
    var lock: String = ""
    
    fun copy(): Node {
        return Node().also { node ->
            node.id = id
            node.name = name
            node.description = description
            node.completionType = completionType
            node.status = status
            node.parentIds = parentIds.toList()
            node.progress = progress
            node.questions = questions?.toList()
            node.ownerId = ownerId
            node.publicAccessLevel = publicAccessLevel
            node.lock = lock
        }
    }

    fun cleanSensitiveData () {
        questions?.forEach{
            if (it.rightAnswer != "") {
                it.rightAnswer = null
            }
        }
    }
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