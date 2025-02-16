package org.aburavov.yourownskilltree.backend.common.model

data class Question (
    val text: String,
    val type: QuestionType,
    val answers: List<String>? = null,
    var rightAnswer: String? = null
)

enum class QuestionType {
    OPEN, CLOSED
}