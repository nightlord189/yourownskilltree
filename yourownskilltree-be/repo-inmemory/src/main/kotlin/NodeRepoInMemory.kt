import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.aburavov.yourownskilltree.backend.common.repo.DbNodeResponse
import org.aburavov.yourownskilltree.backend.common.repo.DbNodesResponse
import org.aburavov.yourownskilltree.backend.common.repo.IRepoNode
import java.util.*

class NodeRepoInMemory (existingNodes: MutableList<Node> = mutableListOf()): IRepoNode {
    private val nodes: MutableList<Node> = existingNodes

    override suspend fun createNode(node: Node): DbNodeResponse {
        val dbEntity = node.copy()
        dbEntity.id = UUID.randomUUID().toString()
        dbEntity.lock = UUID.randomUUID().toString()
        nodes.add(dbEntity)
        return DbNodeResponse(dbEntity)
    }

    override suspend fun readNode(id: String): DbNodeResponse {
        val dbEntity = nodes.firstOrNull() { it.id == id }
        if (dbEntity == null) {
            return DbNodeResponse(errors = listOf(CommonError(message = "not found")))
        }
        return DbNodeResponse(dbEntity)
    }

    override suspend fun updateNode(node: Node): DbNodeResponse {
        val index = nodes.indexOfFirst { it.id == node.id }
        if (index == -1) {
            return DbNodeResponse(errors = listOf(CommonError(message = "not found")))
        }

        val existingNode = nodes[index]
        if (existingNode.lock != node.lock) {
            return DbNodeResponse(errors = listOf(CommonError(message = "invalid lock")))
        }

        val updatedNode = node.copy()
        updatedNode.lock = UUID.randomUUID().toString() // Обновляем lock при каждом изменении
        nodes[index] = updatedNode

        return DbNodeResponse(updatedNode)
    }

    override suspend fun deleteNode(id: String, lock: String): DbNodeResponse {
        val dbEntity = nodes.firstOrNull() { it.id == id }
        if (dbEntity == null) {
            return DbNodeResponse(errors = listOf(CommonError(message = "not found")))
        }
        if (dbEntity.lock != lock) {
            return DbNodeResponse(errors = listOf(CommonError(message = "invalid lock")))
        }
        nodes.remove(dbEntity)
        return DbNodeResponse(dbEntity)
    }

    override suspend fun searchNode(filter: NodeFilter): DbNodesResponse {
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
        return DbNodesResponse(filtered)
    }
}