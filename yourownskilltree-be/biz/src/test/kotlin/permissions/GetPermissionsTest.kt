package permissions

import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.biz.auth.fullPermissions
import org.aburavov.yourownskilltree.backend.biz.auth.getPermissionsToSingleNode
import org.aburavov.yourownskilltree.backend.common.model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import org.aburavov.yourownskilltree.backend.common.permissions.Permission
import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetPermissionsTest {
    @Test
    fun admin() = runBlocking {
        val res = getPermissionsToSingleNode("1", UserGroup.ADMIN, null, null)
        assertEquals(fullPermissions, res)
    }

    @Test
    fun owner() = runBlocking {
        val node = Node().apply {
            ownerId = "1"
        }
        val res = getPermissionsToSingleNode(node.ownerId, UserGroup.USER, node, null)
        assertEquals(fullPermissions, res)
    }

    @Test
    fun userCreate() = runBlocking {
        val res = getPermissionsToSingleNode("1", UserGroup.USER, null, null)
        assertEquals(setOf(Permission.CREATE), res)
    }

    @Test
    fun `user public full access`() = runBlocking {
        val node = Node().apply {
            publicAccessLevel = NodeAccessLevel.FULL_ACCESS
        }
        val res = getPermissionsToSingleNode("2", UserGroup.USER, node, null)
        assertEquals(fullPermissions, res)
    }

    @Test
    fun `user public full read`() = runBlocking {
        val node = Node().apply {
            publicAccessLevel = NodeAccessLevel.FULL_READ
        }
        val res = getPermissionsToSingleNode("2", UserGroup.USER, node, null)
        assertEquals(setOf(Permission.CREATE, Permission.READ, Permission.READ_FULL), res)
    }

    @Test
    fun `guest public full access`() = runBlocking {
        val node = Node().apply {
            publicAccessLevel = NodeAccessLevel.FULL_ACCESS
        }
        val res = getPermissionsToSingleNode(null, UserGroup.GUEST, node, null)
        assertEquals(setOf(Permission.READ), res)
    }

    @Test
    fun `guest public full read`() = runBlocking {
        val node = Node().apply {
            publicAccessLevel = NodeAccessLevel.FULL_READ
        }
        val res = getPermissionsToSingleNode(null, UserGroup.GUEST, node, null)
        assertEquals(setOf(Permission.READ), res)
    }

    @Test
    fun `permission full access`() = runBlocking {
        val node = Node().apply { id = "node1" }
        val accessEntity = AccessEntity(
            userId = "1",
            resourceId = node.id,
            accessLevel = NodeAccessLevel.FULL_ACCESS,
        )
        val res = getPermissionsToSingleNode(accessEntity.userId, UserGroup.USER, node, accessEntity)
        assertEquals(fullPermissions, res)
    }

    @Test
    fun `permission read`() = runBlocking {
        val node = Node().apply { id = "node1" }
        val accessEntity = AccessEntity(
            userId = "1",
            resourceId = node.id,
            accessLevel = NodeAccessLevel.GENERAL_READ,
        )
        val res = getPermissionsToSingleNode(accessEntity.userId, UserGroup.USER, node, accessEntity)
        assertEquals(setOf(Permission.CREATE, Permission.READ), res)
    }

    @Test
    fun `permission full read`() = runBlocking {
        val node = Node().apply { id = "node1" }
        val accessEntity = AccessEntity(
            userId = "1",
            resourceId = node.id,
            accessLevel = NodeAccessLevel.FULL_READ,
        )
        val res = getPermissionsToSingleNode(accessEntity.userId, UserGroup.USER, node, accessEntity)
        assertEquals(setOf(Permission.CREATE, Permission.READ, Permission.READ_FULL), res)
    }
}