import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import repo.*
import java.util.*

class NodeRepoInMemory (existingNodes: MutableList<Node> = mutableListOf()): IRepoNode {
    private val nodes: MutableList<Node> = existingNodes

    override suspend fun createNode(node: Node): IDbNodeResponse {
        val dbEntity = node.copy()
        dbEntity.id = UUID.randomUUID().toString()
        dbEntity.lock = UUID.randomUUID().toString()
        nodes.add(dbEntity)
        return DbNodeResponseOk(dbEntity)
    }

    override suspend fun readNode(id: String): IDbNodeResponse {
        val dbEntity = nodes.firstOrNull() { it.id == id }
        if (dbEntity == null) {
            return DbNodeResponseErr(CommonError(message = "not found"))
        }
        return DbNodeResponseOk(dbEntity)
    }

    override suspend fun updateNode(node: Node): IDbNodeResponse {
        val existingNodeIndex = nodes.indexOfFirst { it.id == node.id }
        if (existingNodeIndex == -1) {
            return DbNodeResponseErr(CommonError(message = "not found"))
        }

        val existingNode = nodes[existingNodeIndex]
        if (existingNode.lock != node.lock) {
            return DbNodeResponseErr(CommonError(message = "invalid lock"))
        }

        val updatedNode = node.copy()
        updatedNode.lock = UUID.randomUUID().toString() // Обновляем lock при каждом изменении
        nodes[existingNodeIndex] = updatedNode

        return DbNodeResponseOk(updatedNode)
    }

    override suspend fun deleteNode(id: String, lock: String): IDbNodeResponse {
        val dbEntity = nodes.firstOrNull() { it.id == id }
        if (dbEntity == null) {
            return DbNodeResponseErr(CommonError(message = "not found"))
        }
        if (dbEntity.lock != lock) {
            return DbNodeResponseErr(CommonError(message = "invalid lock"))
        }
        nodes.remove(dbEntity)
        return DbNodeResponseOk(dbEntity)
    }

    override suspend fun searchNode(filter: NodeFilter): IDbNodesResponse {
        val filtered = nodes.toList().
        filter {
            node -> if (filter.parentId != null)
            node.parentIds.contains(filter.parentId ?: "")
            else true
        }.filter {
            node -> if (filter.nameLike != null)
                node.name.contains(filter.nameLike ?: "", ignoreCase = true)
            else true
        }
        return DbNodesResponseOk(filtered)
    }
}