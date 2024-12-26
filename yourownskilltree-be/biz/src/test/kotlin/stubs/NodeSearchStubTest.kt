package stubs

import kotlinx.coroutines.runBlocking
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeSearchStubTest {
    private val processor = NodeProcessor(mapOf(
        WorkMode.STUB to NodeRepoStub(),
    ))

    @Test
    fun `search success`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.SEARCH
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeFilterRequest = NodeFilter(
                nameLike = "Docker"
            )
        }

        processor.process(context)

        context.errors.any { it.message.contains("lock must be filled") }
        context.nodesResponse?.any{it.name.contains(context.nodeFilterRequest?.nameLike ?: "")}

        assertTrue(context.errors.isEmpty())
        assertNotNull(context.nodesResponse)
        assertTrue  (context.nodesResponse?.count() == 1)
    }

    @Test
    fun `not found`() = runBlocking{
        val context = NodeContext().apply {
            command = NodeCommand.SEARCH
            workMode = WorkMode.STUB
            stubCase = NodeStubs.NOT_FOUND
            nodeFilterRequest = NodeFilter(
                nameLike = "Docker"
            )
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("not found") }) },
            { assertNull(context.nodesResponse) },
        )
    }

    @Test
    fun `db error`()= runBlocking {
        val context = NodeContext().apply {
            command = NodeCommand.SEARCH
            workMode = WorkMode.STUB
            stubCase = NodeStubs.DB_ERROR
            nodeFilterRequest = NodeFilter(
                nameLike = "Docker"
            )
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("db error") }) },
            { assertNull(context.nodesResponse) },
        )
    }
}