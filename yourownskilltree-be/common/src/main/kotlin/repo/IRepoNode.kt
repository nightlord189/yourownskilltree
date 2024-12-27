package repo

import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter

interface IRepoNode {
    suspend fun createNode(node: Node): IDbNodeResponse
    suspend fun readNode(id: String): IDbNodeResponse
    suspend fun updateNode(node: Node): IDbNodeResponse
    suspend fun deleteNode(id: String, lock: String): IDbNodeResponse
    suspend fun searchNode(filter: NodeFilter): IDbNodesResponse
}
