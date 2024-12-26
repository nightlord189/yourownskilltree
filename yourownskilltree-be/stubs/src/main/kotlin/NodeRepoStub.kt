package org.aburavov.yourownskilltree.backend.stubs

import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import repo.*
import java.util.*

class NodeRepoStub: IRepoNode {
    private val stubNode = Node().apply {
        id = UUID.randomUUID().toString()
        name = "Node 1"
        description = "Cool node"
        completionType = NodeCompletionType.PERCENTAGE
        status = NodeStatus.OPEN
        parentIds = emptyList()
        progress = 50
        questions = null
        lock = UUID.randomUUID().toString()
    }

    override suspend fun createNode(node: Node): IDbNodeResponse {
        return DbNodeResponseOk(data = stubNode)
    }

    override suspend fun readNode(id: String): IDbNodeResponse {
        return DbNodeResponseOk(data = stubNode)
    }

    override suspend fun updateNode(node: Node): IDbNodeResponse {
        return DbNodeResponseOk(data = stubNode)
    }

    override suspend fun deleteNode(id: String, lock: String): IDbNodeResponse {
        return DbNodeResponseOk(data = stubNode)
    }

    override suspend fun searchNode(filter: NodeFilter): IDbNodesResponse {
        return DbNodesResponseOk(data = listOf(stubNode))
    }

}