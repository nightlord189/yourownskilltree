import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import repo.DbNodeResponseErr
import repo.DbNodeResponseOk
import repo.DbNodesResponseOk
import java.util.UUID
import kotlin.test.assertNotNull

@Testcontainers
class NodeRepoMongoTest {

    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:6.0"))
            .apply { start() }

        private val logger = KotlinLogging.logger {}
    }

    private lateinit var repo: NodeRepoMongo

    @BeforeEach
    fun setUp() {
        repo = NodeRepoMongo(
            config = MongoConfig(
                host = mongoDBContainer.host,
                port = mongoDBContainer.getMappedPort(27017),
                database = "test_db"
            ),
        )
    }

    @AfterEach
    fun tearDown() = runBlocking {
        repo.close()
    }

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
    fun `test create node`() = runBlocking {
        // given
        val node = getTestNode()

        // when
        val result = repo.createNode(node)

        // then
        assertTrue(result is DbNodeResponseOk)
        result as DbNodeResponseOk
        assertNotNull(result.data)
        assertEquals(node.name, result.data.name)

        // verify changes were persisted
        val readResult = repo.readNode(result.data.id)
        assertTrue(readResult is DbNodeResponseOk)
    }

    @Test
    fun `test read node`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node)

        println(res)

        // when
        val result = repo.readNode((res as DbNodeResponseOk).data.id)

        // then
        assertTrue(result is DbNodeResponseOk)
        result as DbNodeResponseOk
        assertEquals(node.name, result.data.name)
    }

    @Test
    fun `test read non-existent node`() = runBlocking {
        // when
        val result = repo.readNode("non-existent-id")

        // then
        assertTrue(result is DbNodeResponseErr)
        result as DbNodeResponseErr
        assertEquals("not found", result.errors.first().message)
    }

    @Test
    fun `test update node`() = runBlocking {
        // given
        val node = getTestNode()
        val res = repo.createNode(node)

        val updatedNode = node.copy().apply {
            id = (res as DbNodeResponseOk).data.id
            name = "Updated Name"
            lock = res.data.lock
        }

        // when
        val updateResult = repo.updateNode(updatedNode)

        // then
        assertTrue(updateResult is DbNodeResponseOk)
        updateResult as DbNodeResponseOk
        assertEquals(updatedNode.name, updateResult.data.name)

        // verify changes were persisted
        val readResult = repo.readNode(updatedNode.id)
        assertTrue(readResult is DbNodeResponseOk)
        readResult as DbNodeResponseOk
        assertEquals("Updated Name", readResult.data.name)
        assertNotEquals(updatedNode.lock, readResult.data.lock)
    }

    @Test
    fun `test update with invalid lock`() = runBlocking {
        // given
        val node = getTestNode()
        val res = (repo.createNode(node) as DbNodeResponseOk)

        val updatedNode = node.copy().apply {
            id = res.data.id
            lock = "invalid-lock"
        }

        // when
        val result = repo.updateNode(updatedNode)

        // then
        assertTrue(result is DbNodeResponseErr)
        result as DbNodeResponseErr
        assertEquals("invalid lock", result.errors.first().message)
    }

    @Test
    fun `test delete node`() = runBlocking {
        // given
        val node = getTestNode()
        val res = (repo.createNode(node) as DbNodeResponseOk)

        // when
        val result = repo.deleteNode(res.data.id, res.data.lock)

        // then
        assertTrue(result is DbNodeResponseOk)

        // verify node is deleted
        val readResult = repo.readNode(node.id)
        assertTrue(readResult is DbNodeResponseErr)
    }

    @Test
    fun `test delete with invalid lock`() = runBlocking {
        // given
        val node = getTestNode()
        val res = (repo.createNode(node) as DbNodeResponseOk)

        // when
        val result = repo.deleteNode(res.data.id, "invalid-lock")

        // then
        assertTrue(result is DbNodeResponseErr)
        result as DbNodeResponseErr
        assertEquals("invalid lock", result.errors.first().message)
    }

    @Test
    fun `test search by name`() = runBlocking {
        // given
        val node1 = getTestNode(name = "Alpha Node")
        val node2 = getTestNode(name = "Beta Node")
        val node3 = getTestNode(name = "Gamma Node")

        repo.createNode(node1)
        repo.createNode(node2)
        repo.createNode(node3)

        // when
        val result = repo.searchNode(NodeFilter(nameLike = "alpha"))

        // then
        assertTrue(result is DbNodesResponseOk)
        result as DbNodesResponseOk
        assertEquals(1, result.data.size)
        assertEquals("Alpha Node", result.data.first().name)
    }

    @Test
    fun `test search by parent id`() = runBlocking {
        // given
        val parentId = UUID.randomUUID().toString()
        val node1 = getTestNode(parentIds = listOf(parentId))
        val node2 = getTestNode()

        repo.createNode(node1)
        repo.createNode(node2)

        // when
        val result = repo.searchNode(NodeFilter(parentId = parentId))

        // then
        assertTrue(result is DbNodesResponseOk)
        result as DbNodesResponseOk
        assertEquals(1, result.data.size)
    }

    @Test
    fun `test search with both name and parent id`() = runBlocking {
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
        assertTrue(result is DbNodesResponseOk)
        result as DbNodesResponseOk
        assertEquals(1, result.data.size)
        val foundNode = result.data.first()
        assertEquals("Alpha Node", foundNode.name)
        assertTrue(parentId in foundNode.parentIds)
    }
}