package org.aburavov.yourownskilltree.backend.common.mapper

import com.yourownskilltree.backend.api.model.Question as TransportQuestion
import com.yourownskilltree.backend.api.model.Question.Type as TransportQuestionType
import org.aburavov.yourownskilltree.backend.common.model.Question as DomainQuestion
import org.aburavov.yourownskilltree.backend.common.model.QuestionType as DomainQuestionType

object QuestionMapper {
    fun toTransport(domain: DomainQuestion): TransportQuestion {
        return TransportQuestion(
            text = domain.text,
            type = domain.type.toTransport(),
            answers = domain.answers,
            rightAnswer = domain.rightAnswer
        )
    }

    fun toDomain(transport: TransportQuestion): DomainQuestion {
        return DomainQuestion(
            text = requireNotNull(transport.text) { "Question text cannot be null" },
            type = requireNotNull(transport.type) { "Question type cannot be null" }.toDomain(),
            answers = transport.answers,
            rightAnswer = transport.rightAnswer
        )
    }

    fun List<DomainQuestion>.toTransport(): List<TransportQuestion> = map { toTransport(it) }

    fun List<TransportQuestion>.toDomain(): List<DomainQuestion> = map { toDomain(it) }

    private fun DomainQuestionType.toTransport(): TransportQuestionType = when (this) {
        DomainQuestionType.OPEN -> TransportQuestionType.OPEN
        DomainQuestionType.CLOSED -> TransportQuestionType.CLOSED
    }

    private fun TransportQuestionType.toDomain(): DomainQuestionType = when (this) {
        TransportQuestionType.OPEN -> DomainQuestionType.OPEN
        TransportQuestionType.CLOSED -> DomainQuestionType.CLOSED
    }
}