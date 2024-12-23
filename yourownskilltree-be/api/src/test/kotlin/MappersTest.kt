package org.aburavov.yourownskilltree.backend.api.model

import org.aburavov.yourownskilltree.backend.api.mappers.fromTransport
import org.aburavov.yourownskilltree.backend.api.mappers.toTransportCreate
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.common.model.Node as DomainNode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class MappersTest {

    @Test
    fun fromTransport () {
        val request = NodeCreateRequest(
            requestType = "create",
            debug = Debug(
                mode = Debug.Mode.TEST,
                stub = Debug.Stub.SUCCESS
            ),
            node = Node(
                id = "123",
                name = "Test Node",
                completionType = Node.CompletionType.TEST,
                status = Node.Status.IN_PROGRESS,
                description = "Test Description",
                parentIds = listOf("parent1", "parent2"),
                progress = 75,
                questions = listOf(
                    Question(
                        text = "Test Question",
                        type = Question.Type.OPEN,
                        answers = listOf("answer1", "answer2"),
                        rightAnswer = "answer1"
                    )
                )
            )
        )

        // when
        val context = NodeContext()
        context.fromTransport(request)

        // then
        assertEquals(NodeCommand.CREATE, context.command)

        // проверяем маппинг node
        with(context.nodeRequest) {
            assertEquals("Test Node", this?.name ?: "")
            assertEquals(NodeCompletionType.TEST, this?.completionType ?: NodeCompletionType.BOOL)
            assertEquals(NodeStatus.IN_PROGRESS, this?.status ?: NodeStatus.OPEN)
            assertEquals("Test Description",this?.description)
            assertEquals(listOf("parent1", "parent2"), this?.parentIds)
            assertEquals(75, this?.progress)

            // проверяем маппинг вложенных questions
            assertNotNull(this?.questions)
            assertEquals(1, this?.questions?.size)
            this?.questions?.first()?.let { question ->
                assertEquals("Test Question", question.text)
                assertEquals(QuestionType.OPEN, question.type)
                assertEquals(listOf("answer1", "answer2"), question.answers)
                assertEquals("answer1", question.rightAnswer)
            }
        }

        // проверяем маппинг debug
        assertEquals(WorkMode.TEST, context.workMode)
        assertEquals(NodeStubs.SUCCESS, context.stubCase)
    }

    @Test
    fun toTransport() {
        val context = NodeContext().apply{
            requestId = "1234"
            command = NodeCommand.CREATE
            nodeResponse = DomainNode().apply {
                id = "1"
                name = "node1"
                description = "desc"
                completionType = NodeCompletionType.BOOL
                status = NodeStatus.OPEN
            }
            errors = mutableListOf(
                CommonError(
                    code = "err",
                    group = "request",
                    field = "title",
                    message = "wrong title",
                )
            )
        }

        val req = context.toTransportCreate() as NodeCreateResponse

        assertEquals("node1", req.node?.name)
        assertEquals("desc", req.node?.description)
        assertEquals("open", req.node?.status?.value)
        assertEquals(1, req.errors?.size)
        assertEquals("err", req.errors?.firstOrNull()?.code)
        assertEquals("request", req.errors?.firstOrNull()?.group)
        assertEquals("title", req.errors?.firstOrNull()?.field)
        assertEquals("wrong title", req.errors?.firstOrNull()?.message)
    }
}