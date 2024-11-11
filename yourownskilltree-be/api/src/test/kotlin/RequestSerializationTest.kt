package com.yourownskilltree.backend.api.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NodeCreateRequestSerializationTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper().apply {
            registerKotlinModule()
        }
    }

    @Test
    fun `should serialize and deserialize complete request correctly`() {
        // given
        val request = NodeCreateRequest(
            requestType = "create",
            debug = Debug(
                Debug.Mode.STUB,
                Debug.Stub.SUCCESS,
            ),
            node = Node(
                id = "",
                name = "Docker Basics",
                description = "Learn Docker fundamentals",
                completionType = Node.CompletionType.TEST,
                status = Node.Status.IN_PROGRESS,
                parentIds = listOf("containers-101"),
                questions = listOf(
                    Question(
                        text = "What is Docker?",
                        type = Question.Type.OPEN,
                        rightAnswer = "A containerization platform"
                    )
                )
            )
        )

        // when
        val json = objectMapper.writeValueAsString(request)
        val deserializedRequest = objectMapper.readValue(json, NodeCreateRequest::class.java)

        // then
        assertThat(deserializedRequest).usingRecursiveComparison().isEqualTo(request)
    }

    @Test
    fun `should deserialize from JSON string correctly`() {
        // given
        val json = """
            {
                "requestType": "create",
                "debug": {
                    "mode": "stub",
                    "stub": "success"
                },
                "node": {
                    "name": "Docker Basics",
                    "completion_type": "test",
                    "status": "in_progress",
                    "description": "Learn Docker fundamentals",
                    "parent_ids": ["containers-101"],
                    "questions": [
                        {
                            "text": "What is Docker?",
                            "type": "open",
                            "right_answer": "A containerization platform"
                        }
                    ]
                }
            }
        """.trimIndent()

        // when
        val request = objectMapper.readValue(json, NodeCreateRequest::class.java)

        // then
        assertThat(request.requestType).isEqualTo("create")
        assertThat(request.debug).isNotNull
        assertThat(request.node).isNotNull
        assertThat(request.node?.name).isEqualTo("Docker Basics")
        assertThat(request.node?.completionType).isEqualTo(Node.CompletionType.TEST)
        assertThat(request.node?.questions).hasSize(1)
    }

    @Test
    fun `should serialize to JSON string correctly`() {
        // given
        val request = NodeCreateRequest(
            requestType = "create",
            node = Node(
                id = "",
                name = "Simple Node",
                completionType = Node.CompletionType.BOOL,
                status = Node.Status.CLOSED
            )
        )

        // when
        val json = objectMapper.writeValueAsString(request)

        // then
        assertThat(json).contains("\"requestType\":\"create\"")
        assertThat(json).contains("\"name\":\"Simple Node\"")
        assertThat(json).contains("\"completion_type\":\"bool\"")
        assertThat(json).contains("\"status\":\"closed\"")
    }
}