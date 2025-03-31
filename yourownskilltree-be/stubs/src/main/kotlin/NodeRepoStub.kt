package org.aburavov.yourownskilltree.backend.stubs

import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.aburavov.yourownskilltree.backend.common.repo.DbNodeResponse
import org.aburavov.yourownskilltree.backend.common.repo.DbNodesResponse
import org.aburavov.yourownskilltree.backend.common.repo.IRepoNode
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

    override suspend fun createNode(node: Node): DbNodeResponse {
        return DbNodeResponse(data = stubNode)
    }

    override suspend fun readNode(id: String): DbNodeResponse {
        return DbNodeResponse(data = stubNode)
    }

    override suspend fun updateNode(node: Node): DbNodeResponse {
        return DbNodeResponse(data = stubNode)
    }

    override suspend fun deleteNode(id: String, lock: String): DbNodeResponse {
        return DbNodeResponse(data = stubNode)
    }

    override suspend fun searchNode(filter: NodeFilter): DbNodesResponse {
        return DbNodesResponse(data = listOf(stubNode))
    }

}