import kotlinx.coroutines.test.runTest
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repo.DbNodeResponseErr
import repo.DbNodeResponseOk
import repo.DbNodesResponseOk

class NodeRepoInMemoryTest {
    @Test
    fun create () = runTest {
        val repo = NodeRepoInMemory()
        val node = Node().apply {
            name = "node-create"
        }

        val resp = repo.createNode(node)

        assertTrue(resp is DbNodeResponseOk)
        val respNode = (resp as DbNodeResponseOk).data
        assertTrue(respNode.id.isNotEmpty())
        assertTrue(respNode.lock.isNotEmpty())
        assertEquals(node.name, respNode.name)
    }

    @Test
    fun `read success` () = runTest {
        val node = Node().apply {
            id = "id1"
            name = "node-read"
        }
        val repo = NodeRepoInMemory(mutableListOf(node))

        val resp = repo.readNode(node.id)

        assertTrue(resp is DbNodeResponseOk)
        val respNode = (resp as DbNodeResponseOk).data
        assertTrue(respNode.id.isNotEmpty())
        assertEquals(node.name, respNode.name)
    }

    @Test
    fun `read not found` () = runTest {
        val repo = NodeRepoInMemory()

        val resp = repo.readNode("31")

        assertTrue(resp is DbNodeResponseErr)
        assertTrue((resp as DbNodeResponseErr).errors.isNotEmpty())
        assertTrue(resp.errors.any { it.message== "not found" })
    }

    @Test
    fun `update success` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-update"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val nodeToUpdate = existingNode.copy()
        nodeToUpdate.name = "updated"

        val resp = repo.updateNode(nodeToUpdate)

        assertTrue(resp is DbNodeResponseOk)
        val respNode = (resp as DbNodeResponseOk).data
        assertEquals(nodeToUpdate.name, respNode.name)
        assertNotEquals(existingNode.lock, respNode.lock)
    }

    @Test
    fun `update not found` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-update"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val nodeToUpdate = existingNode.copy()
        nodeToUpdate.id = "5"
        nodeToUpdate.name = "updated"

        val resp = repo.updateNode(nodeToUpdate)

        assertTrue(resp is DbNodeResponseErr)
        assertTrue((resp as DbNodeResponseErr).errors.isNotEmpty())
        assertTrue(resp.errors.any { it.message== "not found" })
    }

    @Test
    fun `update - invalid lock` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-update"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val nodeToUpdate = existingNode.copy()
        nodeToUpdate.lock = "4"
        nodeToUpdate.name = "updated"

        val resp = repo.updateNode(nodeToUpdate)

        assertTrue(resp is DbNodeResponseErr)
        assertTrue((resp as DbNodeResponseErr).errors.isNotEmpty())
        assertTrue(resp.errors.any { it.message== "invalid lock" })
    }

    @Test
    fun `delete success` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-delete"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val resp = repo.deleteNode(existingNode.id, existingNode.lock)

        assertTrue(resp is DbNodeResponseOk)
    }

    @Test
    fun `delete not found` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-delete"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val resp = repo.deleteNode("5", existingNode.lock)

        assertTrue(resp is DbNodeResponseErr)
        assertTrue((resp as DbNodeResponseErr).errors.isNotEmpty())
        assertTrue(resp.errors.any { it.message== "not found" })
    }

    @Test
    fun `delete - invalid lock` () = runTest {
        val existingNode = Node().apply {
            id="1"
            name = "node-delete"
            lock = "3"
        }
        val repo = NodeRepoInMemory(mutableListOf(existingNode))

        val resp = repo.deleteNode(existingNode.id, "5")

        assertTrue(resp is DbNodeResponseErr)
        assertTrue((resp as DbNodeResponseErr).errors.isNotEmpty())
        assertTrue(resp.errors.any { it.message== "invalid lock" })
    }

    @Test
    fun search () = runTest {
        val repo = NodeRepoInMemory(
            mutableListOf(
                Node().apply {
                    id="1"
                    name = "Docker Setup"
                    parentIds = listOf("parent1", "parent2")
                },
                Node().apply {
                    id="2"
                    name = "Kubernetes"
                    parentIds = listOf("parent3")
                },
                Node().apply {
                    id="3"
                    name = "PostgreSQL Setup for Beginners"
                    parentIds = listOf("parent1")
                }
            )
        )

        val resp = repo.searchNode(NodeFilter(parentId = "parent1", nameLike = "Setup"))

        assertTrue(resp is DbNodesResponseOk)
        val respNodes = (resp as DbNodesResponseOk).data
        assertTrue(respNodes.count() == 2)
        assertTrue(respNodes.any { it.id=="1" })
        assertTrue(respNodes.any { it.id=="3" })
    }
}