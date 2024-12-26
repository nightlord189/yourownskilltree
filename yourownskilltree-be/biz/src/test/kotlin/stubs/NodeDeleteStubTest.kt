package stubs

import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.NodeCommand
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.NodeStubs
import org.aburavov.yourownskilltree.backend.common.model.WorkMode
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeDeleteStubTest {
    private val processor = NodeProcessor(mapOf(
        WorkMode.STUB to NodeRepoStub(),
    ))

    @Test
    fun `delete success`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.DELETE
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeIdRequest = "id1"
            nodeLock = "lock1"
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isEmpty()) },
            { assertNotNull(context.nodeResponse) },
            { assertNotNull(context.nodeResponse?.id) },
            { assertNotNull(context.nodeResponse?.name) },
            { assertNotNull(context.nodeResponse?.lock) },
        )
    }

    @Test
    fun `not found`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.DELETE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.NOT_FOUND
            nodeIdRequest = "id1"
            nodeLock = "lock1"
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
            command = NodeCommand.DELETE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.BAD_ID
            nodeIdRequest = "id1"
            nodeLock = "lock1"
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("bad id") }) }
        )
    }

    @Test
    fun `cannot delete`()= runBlocking {
        val context = NodeContext().apply {
            command = NodeCommand.DELETE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.CANNOT_DELETE
            nodeIdRequest = "id1"
            nodeLock = "lock1"
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("cannot delete") }) }
        )
    }

    @Test
    fun `db error`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.DELETE
            workMode = WorkMode.STUB
            stubCase= NodeStubs.DB_ERROR
            nodeIdRequest = "id1"
            nodeLock = "lock1"
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("db error") }) }
        )
    }
}