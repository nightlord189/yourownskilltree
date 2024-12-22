package stubs

import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeUpdateStubTest {
    private val processor = NodeProcessor()

    @Test
    fun `update success`() {
        val context = NodeContext().apply {
            command = NodeCommand.UPDATE
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeRequest = Node().apply {
                id = "id1"
                name = "Test Node"
                status = NodeStatus.COMPLETED
                lock = "lock1"
            }
        }

        processor.process(context)

        println("${context.nodeRequest?.lock}, ${context.nodeResponse?.lock}")

        assertAll(
            { assertTrue(context.errors.isEmpty()) },
            { assertNotNull(context.nodeResponse) },
            { assertNotNull(context.nodeResponse?.id) },
            { assertEquals(context.nodeRequest?.name, context.nodeResponse?.name) },
            { assertEquals(context.nodeRequest?.status, context.nodeResponse?.status) },
            { assertNotEquals(context.nodeRequest?.lock, context.nodeResponse?.lock) }
        )
    }

    @Test
    fun `not found`() {
        val context = NodeContext().apply {
            command = NodeCommand.UPDATE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.NOT_FOUND
            nodeRequest = Node().apply {
                id = "id1"
                name = "Test Node"
                status = NodeStatus.COMPLETED
                lock = "lock1"
            }
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("not found") }) }
        )
    }

    @Test
    fun `bad id`() {
        val context = NodeContext().apply {
            command = NodeCommand.UPDATE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.BAD_ID
            nodeRequest = Node().apply {
                id = "id1"
                name = "Test Node"
                status = NodeStatus.COMPLETED
                lock = "lock1"
            }
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("bad id") }) }
        )
    }

    @Test
    fun `db error`() {
        val context = NodeContext().apply {
            command = NodeCommand.UPDATE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.DB_ERROR
            nodeRequest = Node().apply {
                id = "id1"
                name = "Test Node"
                status = NodeStatus.COMPLETED
                lock = "lock1"
            }
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("db error") }) }
        )
    }
}