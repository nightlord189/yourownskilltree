package org.aburavov.yourownskilltree.backend.repo.mongo

import org.aburavov.yourownskilltree.backend.common.model.*
import org.bson.types.ObjectId
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class MongoNode(
    @BsonId
    @BsonProperty("_id")
    var mongoId: ObjectId = ObjectId(),
    var id: String,
    val name: String,
    val description: String?,
    val completionType: String,
    val status: String,
    val parentIds: List<String>,
    val progress: Int?,
    val questions: List<MongoQuestion>?,
    var lock: String
) {
    companion object {
        fun fromCommon(node: Node): MongoNode = MongoNode(
            id = node.id,
            name = node.name,
            description = node.description,
            completionType = node.completionType.name,
            status = node.status.name,
            parentIds = node.parentIds,
            progress = node.progress,
            questions = node.questions?.map { MongoQuestion.fromCommon(it) },
            lock = node.lock
        )
    }

    fun toCommon(): Node = Node().also { node ->
        node.id = id
        node.name = name
        node.description = description
        node.completionType = NodeCompletionType.valueOf(completionType)
        node.status = NodeStatus.valueOf(status)
        node.parentIds = parentIds
        node.progress = progress
        node.questions = questions?.map { it.toCommon() }
        node.lock = lock
    }
}

data class MongoQuestion(
    val text: String,
    val type: String,
    val answers: List<String>?,
    val rightAnswer: String?
) {
    companion object {
        fun fromCommon(question: Question): MongoQuestion = MongoQuestion(
            text = question.text,
            type = question.type.name,
            answers = question.answers,
            rightAnswer = question.rightAnswer
        )
    }

    fun toCommon(): Question = Question(
        text = text,
        type = QuestionType.valueOf(type),
        answers = answers,
        rightAnswer = rightAnswer
    )
}
