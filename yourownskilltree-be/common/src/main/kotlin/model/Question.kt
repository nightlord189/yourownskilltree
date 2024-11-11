package org.aburavov.yourownskilltree.backend.common.model

data class Question (
    val text: String,
    val type: QuestionType,
    val answers: List<String>? = null,
    val rightAnswer: String? = null
)

enum class QuestionType (val value: String) {
    OPEN ("open"), CLOSED ("closed")
}