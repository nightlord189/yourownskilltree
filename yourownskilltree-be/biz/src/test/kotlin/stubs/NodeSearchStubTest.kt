package stubs

import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NodeSearchStubTest {
    private val processor = NodeProcessor()

    @Test
    fun `search success`() {
        val context = NodeContext().apply {
            command = NodeCommand.SEARCH
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeFilterRequest = NodeFilter(
                nameLike = "Docker"
            )
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isEmpty()) },
            { assertNotNull(context.nodesResponse) },
            { assertTrue  (context.nodesResponse?.count() == 1) },
            { assertTrue(context.nodesResponse!![0].name.contains(context.nodeFilterRequest!!.nameLike!!)) },
        )
    }

    @Test
    fun `not found`() {
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
    fun `db error`() {
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