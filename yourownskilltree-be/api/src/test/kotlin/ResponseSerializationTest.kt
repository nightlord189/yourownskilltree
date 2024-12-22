package org.aburavov.yourownskilltree.backend.api.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NodeCreateResponseSerializationTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper().apply {
            registerKotlinModule()
        }
    }

    @Test
    fun `should serialize and deserialize successful response correctly`() {
        // given
        val response = NodeCreateResponse(
            responseType = "nodeCreated",
            result = ResponseResult.SUCCESS,
            node = Node(
                id = "node-123",
                name = "Docker Basics",
                completionType = Node.CompletionType.TEST,
                status = Node.Status.OPEN,
                description = "Learn Docker fundamentals",
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
        val json = objectMapper.writeValueAsString(response)
        val deserializedResponse = objectMapper.readValue(json, NodeCreateResponse::class.java)

        // then
        assertThat(deserializedResponse).usingRecursiveComparison().isEqualTo(response)
    }

    @Test
    fun `should serialize and deserialize error response correctly`() {
        // given
        val response = NodeCreateResponse(
            responseType = "create",
            result = ResponseResult.ERROR,
            errors = listOf(
                Error(
                    code = "VALIDATION_ERROR",
                    message = "Invalid node data",
                    field = "name"
                )
            )
        )

        // when
        val json = objectMapper.writeValueAsString(response)
        val deserializedResponse = objectMapper.readValue(json, NodeCreateResponse::class.java)

        // then
        assertThat(deserializedResponse).usingRecursiveComparison().isEqualTo(response)
        assertThat(deserializedResponse.errors).hasSize(1)
        assertThat(deserializedResponse.errors?.first()?.code).isEqualTo("VALIDATION_ERROR")
    }

    @Test
    fun `should deserialize from JSON string correctly`() {
        // given
        val json = """
            {
                "responseType": "create",
                "result": "success",
                "node": {
                    "id": "node-123",
                    "name": "Docker Basics",
                    "completion_type": "test",
                    "status": "open",
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
        val response = objectMapper.readValue(json, NodeCreateResponse::class.java)

        // then
        assertThat(response.responseType).isEqualTo("create")
        assertThat(response.result).isEqualTo(ResponseResult.SUCCESS)
        assertThat(response.node).isNotNull
        assertThat(response.node?.id).isEqualTo("node-123")
        assertThat(response.node?.name).isEqualTo("Docker Basics")
        assertThat(response.node?.questions).hasSize(1)
    }
}