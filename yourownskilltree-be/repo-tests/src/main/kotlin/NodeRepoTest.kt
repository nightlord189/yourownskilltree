import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import repo.IRepoNode
import java.util.UUID
import kotlin.test.assertNotNull

abstract class NodeRepoTest {
    abstract var repo: IRepoNode

    private fun getTestNode(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Node",
        parentIds: List<String> = emptyList()
    ): Node = Node().apply {
        this.id = id
        this.name = name
        this.description = "Test Description"
        this.completionType = NodeCompletionType.BOOL
        this.status = NodeStatus.OPEN
        this.parentIds = parentIds
        this.progress = null
        this.questions = null
        this.lock = UUID.randomUUID().toString()
    }

    @Test
    open fun `test create node`() = runBlocking {
        // given
        val node = getTestNode()

        // when
        val result = repo.createNode(node)

        // then
        assertTrue { result.errors.isEmpty() }
        assertNotNull(result.data)
        assertEquals(node.name, result.data?.name)
    }

    @Test
    open fun `test read node`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node).data
        assertNotNull(res)

        // when
        val result = repo.readNode(res.id)

        // then
        assertTrue(result.errors.isEmpty())
        assertEquals(node.name, result.data?.name)
    }

    @Test
    open fun `test read non-existent node`() = runBlocking {
        // when
        val result = repo.readNode("non-existent-id")

        // then
        assertTrue(result.errors.isNotEmpty())
        assertEquals("not found", result.errors.first().message)
    }

    @Test
    open fun `test update node`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node).data
        assertNotNull(res)

        val updatedNode = node.copy().apply {
            id = res.id
            name = "Updated Name"
            lock = res.lock
        }

        // when
        val updateResult = repo.updateNode(updatedNode)

        // then
        assertTrue(updateResult.errors.isEmpty())
        assertEquals(updatedNode.name, updateResult.data?.name)

        // verify changes were persisted
        val readResult = repo.readNode(updatedNode.id)
        assertTrue(readResult.errors.isEmpty())
        assertNotNull(readResult.data)
        assertEquals("Updated Name", readResult.data?.name)
        assertNotEquals(updatedNode.lock, readResult.data?.lock)
    }

    @Test
    open fun `test update with invalid lock`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node).data
        assertNotNull(res)

        val updatedNode = node.copy().apply {
            id = res.id
            lock = "invalid-lock"
        }

        // when
        val result = repo.updateNode(updatedNode)
        assertTrue(result.errors.isNotEmpty())
        assertNull(result.data)
        assertEquals("invalid lock", result.errors.first().message)
    }

    @Test
    open fun `test delete node`() = runBlocking {
        // given
        val node = getTestNode()
        val res =repo.createNode(node).data
        assertNotNull(res)

        // when
        val result = repo.deleteNode(res.id, res.lock)

        // then
        assertTrue(result.errors.isEmpty())

        // verify node is deleted
        val readResult = repo.readNode(node.id)
        assertTrue(readResult.errors.isNotEmpty())
        assertNull(readResult.data)
    }

    @Test
    open fun `test delete with invalid lock`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node).data
        assertNotNull(res)

        // when
        val result = repo.deleteNode(res.id, "invalid-lock")
        assertTrue(result.errors.isNotEmpty())

        // then
        assertEquals("invalid lock", result.errors.first().message)
    }

    @Test
    open fun `test search by name`() = runBlocking {
        // given
        val node1 = getTestNode(name = "Alpha Node")
        val node2 = getTestNode(name = "Beta Node")
        val node3 = getTestNode(name = "Gamma Node")

        repo.createNode(node1)
        repo.createNode(node2)
        repo.createNode(node3)

        // when
        val result = repo.searchNode(NodeFilter(nameLike = "alpha"))
        assertTrue(result.errors.isEmpty())

        // then
        assertEquals(1, result.data?.size)
        assertEquals("Alpha Node", result.data?.first()?.name)
    }

    @Test
    open fun `test search by parent id`() = runBlocking {
        // given
        val parentId = UUID.randomUUID().toString()
        val node1 = getTestNode(parentIds = listOf(parentId))
        val node2 = getTestNode()

        repo.createNode(node1)
        repo.createNode(node2)

        // when
        val result = repo.searchNode(NodeFilter(parentId = parentId))
        assertTrue(result.errors.isEmpty())

        // then
        assertEquals(1, result.data?.size)
    }

    @Test
    open fun `test search with both name and parent id`() = runBlocking {
        // given
        val parentId = UUID.randomUUID().toString()
        val node1 = getTestNode(name = "Alpha Node", parentIds = listOf(parentId))
        val node2 = getTestNode(name = "Alpha Node")
        val node3 = getTestNode(name = "Beta Node", parentIds = listOf(parentId))

        repo.createNode(node1)
        repo.createNode(node2)
        repo.createNode(node3)

        // when
        val result = repo.searchNode(NodeFilter(
            nameLike = "alpha",
            parentId = parentId
        ))

        // then
        assertTrue(result.errors.isEmpty())
        assertEquals(1, result.data?.size)
        val foundNode = result.data?.first()
        assertEquals("Alpha Node", foundNode?.name)
        assertTrue(foundNode?.parentIds?.contains(parentId) == true)
    }
}