import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.common.model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertNotNull
import kotlin.test.assertNull

abstract class AccessEntityRepoTest {
    abstract var repo: IRepoAccessEntity

    private fun getTestItem(
        userId: String,
        resourceId: String,
        accessLevel: NodeAccessLevel = NodeAccessLevel.FULL_ACCESS,
    ): AccessEntity = AccessEntity(
        userId = userId,
        resourceId = resourceId,
        accessLevel = accessLevel,
    )

    @Test
    open fun `test create`(): Unit = runBlocking {
        // given
        val item = getTestItem("create", "res1")

        // when
        val result = repo.put(item)

        // then
        assertTrue { result.errors.isEmpty() }
        assertNotNull(result.data)
    }

    @Test
    open fun `test read`(): Unit = runBlocking {
        val item = getTestItem("read", "res1")

        val result = repo.put(item)

        assertTrue { result.errors.isEmpty() }
        assertNotNull(result.data)

        val readResult = repo.read("read", "res1")
        assertTrue { result.errors.isEmpty() }
        assertNotNull(result.data)
        assertEquals(NodeAccessLevel.FULL_ACCESS, result.data?.accessLevel)
    }


    @Test
    open fun `test update`(): Unit = runBlocking {
        val item = getTestItem("update", "res1", NodeAccessLevel.GENERAL_READ)
        val result = repo.put(item)

        assertTrue { result.errors.isEmpty() }
        assertNotNull(result.data)

        val result2 = repo.put(getTestItem("update", "res1", NodeAccessLevel.FULL_READ))
        assertTrue { result2.errors.isEmpty() }
        assertNotNull(result2.data)
        assertEquals(NodeAccessLevel.FULL_READ, result2.data?.accessLevel)
    }

    @Test
    open fun `test delete`(): Unit = runBlocking {
        val item = getTestItem("delete", "res1", NodeAccessLevel.GENERAL_READ)
        val result = repo.put(item)

        assertTrue { result.errors.isEmpty() }

        val result2 = repo.delete("delete", "res1")
        assertTrue { result2.errors.isEmpty() }

        val result3 = repo.read("delete", "res1")
        assertTrue { result3.errors.isNotEmpty() }
        assertNull(result3.data)
    }
}