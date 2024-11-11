package org.aburavov.yourownskilltree.backend.common.mapper

import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toDomain
import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toTransport
import com.yourownskilltree.backend.api.model.Question as TransportQuestion
import org.aburavov.yourownskilltree.backend.common.model.Question as DomainQuestion
import org.aburavov.yourownskilltree.backend.common.model.QuestionType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class QuestionMapperTest {

    @Test
    fun `should map domain to transport question correctly`() {
        // given
        val domainQuestion = DomainQuestion(
            text = "What is Kotlin?",
            type = QuestionType.OPEN,
            rightAnswer = "Kotlin is a programming language"
        )

        // when
        val transportQuestion = QuestionMapper.toTransport(domainQuestion)

        // then
        assertThat(transportQuestion.text).isEqualTo(domainQuestion.text)
        assertThat(transportQuestion.type).isEqualTo(TransportQuestion.Type.OPEN)
        assertThat(transportQuestion.rightAnswer).isEqualTo(domainQuestion.rightAnswer)
        assertThat(transportQuestion.answers).isNull()
    }

    @Test
    fun `should map closed question with answers correctly`() {
        // given
        val domainQuestion = DomainQuestion(
            text = "What is the capital of France?",
            type = QuestionType.CLOSED,
            answers = listOf("London", "Paris", "Berlin"),
            rightAnswer = "Paris"
        )

        // when
        val transportQuestion = QuestionMapper.toTransport(domainQuestion)

        // then
        assertThat(transportQuestion.text).isEqualTo(domainQuestion.text)
        assertThat(transportQuestion.type).isEqualTo(TransportQuestion.Type.CLOSED)
        assertThat(transportQuestion.answers)
            .containsExactlyElementsOf(domainQuestion.answers)
        assertThat(transportQuestion.rightAnswer).isEqualTo(domainQuestion.rightAnswer)
    }

    @Test
    fun `should map transport to domain question correctly`() {
        // given
        val transportQuestion = TransportQuestion(
            text = "What is Kotlin?",
            type = TransportQuestion.Type.OPEN,
            rightAnswer = "Kotlin is a programming language"
        )

        // when
        val domainQuestion = QuestionMapper.toDomain(transportQuestion)

        // then
        assertThat(domainQuestion.text).isEqualTo(transportQuestion.text)
        assertThat(domainQuestion.type).isEqualTo(QuestionType.OPEN)
        assertThat(domainQuestion.rightAnswer).isEqualTo(transportQuestion.rightAnswer)
        assertThat(domainQuestion.answers).isNull()
    }

    @ParameterizedTest
    @EnumSource(QuestionType::class)
    fun `should map all question types correctly`(domainType: QuestionType) {
        // given
        val domainQuestion = DomainQuestion(
            text = "Test question",
            type = domainType
        )

        // when
        val transportQuestion = QuestionMapper.toTransport(domainQuestion)
        val mappedBackDomainQuestion = QuestionMapper.toDomain(transportQuestion)

        // then
        assertThat(mappedBackDomainQuestion.type).isEqualTo(domainType)
    }

    @Test
    fun `should throw exception when mapping transport question with null text`() {
        // given
        val transportQuestion = TransportQuestion(
            text = null,
            type = TransportQuestion.Type.OPEN
        )

        // then
        assertThatThrownBy {
            QuestionMapper.toDomain(transportQuestion)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Question text cannot be null")
    }

    @Test
    fun `should throw exception when mapping transport question with null type`() {
        // given
        val transportQuestion = TransportQuestion(
            text = "Test",
            type = null
        )

        // then
        assertThatThrownBy {
            QuestionMapper.toDomain(transportQuestion)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Question type cannot be null")
    }

    @Test
    fun `should map list of questions correctly`() {
        // given
        val domainQuestions = listOf(
            DomainQuestion(
                text = "Question 1",
                type = QuestionType.OPEN
            ),
            DomainQuestion(
                text = "Question 2",
                type = QuestionType.CLOSED,
                answers = listOf("A", "B"),
                rightAnswer = "A"
            )
        )

        // when
        val transportQuestions = domainQuestions.toTransport()
        val mappedBackDomainQuestions = transportQuestions.toDomain()

        // then
        assertThat(mappedBackDomainQuestions)
            .usingRecursiveComparison()
            .isEqualTo(domainQuestions)
    }
}