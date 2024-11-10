package com.yourownskilltree.backend.api

import com.yourownskilltree.backend.api.model.Question

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class QuestionSerializationTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper().apply {
            registerKotlinModule()
        }
    }

    @Test
    fun `should serialize and deserialize closed question correctly`() {
        // given
        val question = Question(
            text = "What is the capital of France?",
            type = Question.Type.CLOSED,
            answers = listOf("London", "Paris", "Berlin", "Madrid"),
            rightAnswer = "Paris"
        )

        // when
        val json = objectMapper.writeValueAsString(question)
        val deserializedQuestion = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(deserializedQuestion).usingRecursiveComparison().isEqualTo(question)
        assertThat(json).contains("closed") // проверяем, что тип сериализуется в нижнем регистре
    }

    @Test
    fun `should serialize and deserialize open question correctly`() {
        // given
        val question = Question(
            text = "Explain what is Docker",
            type = Question.Type.OPEN,
            answers = null,
            rightAnswer = "Docker is a platform for developing, shipping, and running applications in containers"
        )

        // when
        val json = objectMapper.writeValueAsString(question)
        val deserializedQuestion = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(deserializedQuestion).usingRecursiveComparison().isEqualTo(question)
        assertThat(json).contains("open")
    }

    @ParameterizedTest
    @EnumSource(Question.Type::class)
    fun `should handle all question types correctly`(type: Question.Type) {
        // given
        val json = """
            {
                "text": "Test question",
                "type": "${type.value}",
                "answers": ["answer1", "answer2"],
                "right_answer": "answer1"
            }
        """.trimIndent()

        // when
        val question = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(question.type).isEqualTo(type)
    }

    @Test
    fun `should handle null optional fields`() {
        // given
        val json = """
            {
                "text": "Test question"
            }
        """.trimIndent()

        // when
        val question = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(question.text).isEqualTo("Test question")
        assertThat(question.type).isNull()
        assertThat(question.answers).isNull()
        assertThat(question.rightAnswer).isNull()
    }

    @Test
    fun `should handle empty answers list`() {
        // given
        val question = Question(
            text = "Test question",
            type = Question.Type.CLOSED,
            answers = emptyList(),
            rightAnswer = "test"
        )

        // when
        val json = objectMapper.writeValueAsString(question)
        val deserializedQuestion = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(deserializedQuestion.answers).isNotNull.isEmpty()
    }

    @Test
    fun `should deserialize from JSON with snake case`() {
        // given
        val json = """
            {
                "text": "Test question",
                "type": "closed",
                "answers": ["one", "two"],
                "right_answer": "one"
            }
        """.trimIndent()

        // when
        val question = objectMapper.readValue(json, Question::class.java)

        // then
        assertThat(question.rightAnswer).isEqualTo("one")
    }

    @Test
    fun `should serialize to JSON with snake case`() {
        // given
        val question = Question(
            text = "Test",
            type = Question.Type.CLOSED,
            answers = listOf("one"),
            rightAnswer = "one"
        )

        // when
        val json = objectMapper.writeValueAsString(question)

        // then
        assertThat(json).contains("right_answer")
        assertThat(json).doesNotContain("rightAnswer")
    }
}