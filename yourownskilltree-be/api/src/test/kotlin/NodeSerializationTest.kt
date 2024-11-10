package com.yourownskilltree.backend.api

import com.yourownskilltree.backend.api.model.Node
import com.yourownskilltree.backend.api.model.Question

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class NodeSerializationTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper().apply {
            registerKotlinModule()
        }
    }

    @Test
    fun `should serialize and deserialize complete node correctly`() {
        // given
        val node = Node(
            name = "Advanced Git",
            completionType = Node.CompletionType.TEST,
            status = Node.Status.IN_PROGRESS,
            description = "Learn advanced Git concepts",
            parentIds = listOf("git-basics", "version-control"),
            progress = 60,
            questions = listOf(
                Question(
                    text = "What is git rebase?",
                    type = Question.Type.OPEN,
                    answers = null,
                    rightAnswer = "Git rebase is a command that integrates changes from one branch into another by moving or combining commits"
                ),
                Question(
                    text = "Which command creates a new branch?",
                    type = Question.Type.CLOSED,
                    answers = listOf("git branch", "git checkout", "git create"),
                    rightAnswer = "git branch"
                )
            )
        )

        // when
        val json = objectMapper.writeValueAsString(node)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode).usingRecursiveComparison().isEqualTo(node)
        assertThat(json).contains("completion_type")
        assertThat(json).contains("parent_ids")
    }

    @Test
    fun `should handle minimal required fields`() {
        // given
        val minimalNode = Node(
            name = "Git Basics",
            completionType = Node.CompletionType.BOOL,
            status = Node.Status.CLOSED
        )

        // when
        val json = objectMapper.writeValueAsString(minimalNode)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode).usingRecursiveComparison().isEqualTo(minimalNode)
        assertThat(deserializedNode.description).isNull()
        assertThat(deserializedNode.parentIds).isNull()
        assertThat(deserializedNode.progress).isNull()
        assertThat(deserializedNode.questions).isNull()
    }

    @ParameterizedTest
    @EnumSource(Node.CompletionType::class)
    fun `should handle all completion types correctly`(completionType: Node.CompletionType) {
        // given
        val node = Node(
            name = "Test Node",
            completionType = completionType,
            status = Node.Status.OPEN
        )

        // when
        val json = objectMapper.writeValueAsString(node)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode.completionType).isEqualTo(completionType)
        assertThat(json).contains(""""completion_type":"${completionType.value}"""")
    }

    @ParameterizedTest
    @EnumSource(Node.Status::class)
    fun `should handle all status types correctly`(status: Node.Status) {
        // given
        val node = Node(
            name = "Test Node",
            completionType = Node.CompletionType.BOOL,
            status = status
        )

        // when
        val json = objectMapper.writeValueAsString(node)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode.status).isEqualTo(status)
        assertThat(json).contains(""""status":"${status.value}"""")
    }

    @Test
    fun `should handle empty lists`() {
        // given
        val node = Node(
            name = "Empty Lists Node",
            completionType = Node.CompletionType.TEST,
            status = Node.Status.OPEN,
            parentIds = emptyList(),
            questions = emptyList()
        )

        // when
        val json = objectMapper.writeValueAsString(node)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode.parentIds).isNotNull.isEmpty()
        assertThat(deserializedNode.questions).isNotNull.isEmpty()
    }

    @Test
    fun `should correctly handle percentage progress`() {
        // given
        val node = Node(
            name = "Progress Node",
            completionType = Node.CompletionType.PERCENTAGE,
            status = Node.Status.IN_PROGRESS,
            progress = 75
        )

        // when
        val json = objectMapper.writeValueAsString(node)
        val deserializedNode = objectMapper.readValue(json, Node::class.java)

        // then
        assertThat(deserializedNode.progress).isEqualTo(75)
    }
}