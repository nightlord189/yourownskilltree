package stubs

import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeUpdateStubTest {
    private val processor = NodeProcessor(mapOf(
        WorkMode.STUB to NodeRepoStub(),
    ))

    @Test
    fun `update success`() = runBlocking{
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

        assertTrue(context.errors.isEmpty())
        assertNotNull(context.nodeResponse)
        assertNotNull(context.nodeResponse?.id)
    }

    @Test
    fun `not found`()= runBlocking {
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
    fun `bad id`() = runBlocking{
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
    fun `db error`() = runBlocking{
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