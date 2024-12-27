package stubs

import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeReadStubTest {
    private val processor = NodeProcessor(mapOf(
        WorkMode.STUB to NodeRepoStub(),
    ))

    @Test
    fun `read success`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.READ
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeIdRequest = "id1"
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
            command = NodeCommand.READ
            workMode = WorkMode.STUB
            stubCase= NodeStubs.NOT_FOUND
            nodeIdRequest = "id1"
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
            command = NodeCommand.READ
            workMode = WorkMode.STUB
            stubCase= NodeStubs.BAD_ID
            nodeIdRequest = "id1"
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
            command = NodeCommand.READ
            workMode = WorkMode.STUB
            stubCase= NodeStubs.DB_ERROR
            nodeIdRequest = "id1"
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("db error") }) }
        )
    }
}