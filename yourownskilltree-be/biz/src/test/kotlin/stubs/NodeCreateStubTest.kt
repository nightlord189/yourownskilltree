package org.aburavov.yourownskilltree.backend.biz

import org.aburavov.yourownskilltree.backend.common.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class NodeCreateStubTest {
    private val processor = NodeProcessor()

    @Test
    fun `create success`() {
        val context = NodeContext().apply {
            command = NodeCommand.CREATE
            workMode = WorkMode.STUB
            stubCase = NodeStubs.SUCCESS
            nodeRequest = Node().apply {
                name = "Test Node"
            }
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isEmpty()) },
            { assertNotNull(context.nodeResponse) },
            { assertNotNull(context.nodeResponse?.id) },
            { assertNotNull(context.nodeResponse?.lock) },
            { assertEquals(context.nodeRequest?.name, context.nodeResponse?.name) },
        )
    }

    @Test
    fun `db error`() {
        val context = NodeContext().apply {
            command = NodeCommand.CREATE
            workMode = WorkMode.STUB
            stubCase=NodeStubs.DB_ERROR
            nodeRequest =Node().apply {
                name = "Test Node"
            }
        }

        processor.process(context)

        assertAll(
            { assertTrue(context.errors.isNotEmpty()) },
            { assertTrue(context.errors.any { it.message.contains("db error") }) }
        )
    }
}