package repo

import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter

interface IRepoNode {
    suspend fun createNode(node: Node): DbResponse<Node>
    suspend fun readNode(id: String): DbResponse<Node>
    suspend fun updateNode(node: Node): DbResponse<Node>
    suspend fun deleteNode(id: String, lock: String): DbResponse<Node>
    suspend fun searchNode(filter: NodeFilter): DbResponse<List<Node>>
}
