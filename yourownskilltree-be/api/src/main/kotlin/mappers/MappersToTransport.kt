package org.aburavov.yourownskilltree.backend.api.mappers

import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.Node as DomainNode
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus as DomainNodeStatus
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType as DomainNodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.QuestionType as DomainQuestionType
import org.aburavov.yourownskilltree.backend.common.model.Question as DomainQuestion
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.api.model.*
import org.aburavov.yourownskilltree.backend.common.model.NodeCommand

fun NodeContext.toTransportNode(): IResponse = when (command) {
    NodeCommand.CREATE -> toTransportCreate()
    NodeCommand.READ -> toTransportRead()
    NodeCommand.UPDATE -> toTransportUpdate()
    NodeCommand.DELETE -> toTransportDelete()
    NodeCommand.SEARCH -> toTransportSearch()
    NodeCommand.NONE -> throw Exception("unknown command")
}

fun NodeContext.toTransportSearch() = NodeSearchResponse (
    responseType = "search",
    errors = errors.toTransportErrors(),
    nodes = nodesResponse.let{ nodesResponse?.map { it.toTransport() }},
    result = if (errors.isEmpty()) ResponseResult.SUCCESS else ResponseResult.ERROR
)

fun NodeContext.toTransportRead() = NodeReadResponse (
    responseType = "read",
    errors = errors.toTransportErrors(),
    node = nodeResponse?.toTransport(),
    result = if (errors.isEmpty()) ResponseResult.SUCCESS else ResponseResult.ERROR
)

fun NodeContext.toTransportCreate() = NodeCreateResponse (
    responseType = "create",
    errors = errors.toTransportErrors(),
    node = nodeResponse?.toTransport(),
    result = if (errors.isEmpty()) ResponseResult.SUCCESS else ResponseResult.ERROR
)

fun NodeContext.toTransportUpdate() = NodeUpdateResponse (
    responseType = "update",
    errors = errors.toTransportErrors(),
    node = nodeResponse?.toTransport(),
    result = if (errors.isEmpty()) ResponseResult.SUCCESS else ResponseResult.ERROR
)

fun NodeContext.toTransportDelete() = NodeDeleteResponse (
    responseType = "delete",
    errors = errors.toTransportErrors(),
    node = nodeResponse?.toTransport(),
    result = if (errors.isEmpty()) ResponseResult.SUCCESS else ResponseResult.ERROR
)

fun DomainNode.toTransport(): Node {
    return Node(
        id = id.takeIf { it.isNotEmpty() },
        name = name,
        completionType = when(completionType) {
            DomainNodeCompletionType.BOOL -> Node.CompletionType.BOOL
            DomainNodeCompletionType.PERCENTAGE -> Node.CompletionType.PERCENTAGE
            DomainNodeCompletionType.TEST -> Node.CompletionType.TEST
        },
        status = when(status) {
            DomainNodeStatus.CLOSED -> Node.Status.CLOSED
            DomainNodeStatus.OPEN -> Node.Status.OPEN
            DomainNodeStatus.IN_PROGRESS -> Node.Status.IN_PROGRESS
            DomainNodeStatus.COMPLETED -> Node.Status.COMPLETED
        },
        description = description,
        parentIds = parentIds.takeIf { it.isNotEmpty() },
        progress = progress,
        questions = questions?.let { questions -> questions.map { it.toTransport() } },
        lock = lock
    )
}

fun DomainQuestion.toTransport(): Question {
    return Question(
        text = text,
        type = when(type) {
            DomainQuestionType.OPEN -> Question.Type.OPEN
            DomainQuestionType.CLOSED -> Question.Type.CLOSED
        },
        answers = answers?.takeIf { it.isNotEmpty() },
        rightAnswer = rightAnswer
    )
}

private fun List<CommonError>.toTransportErrors(): List<Error> = this
    .map { it.toTransport() }
    .toList()

private fun CommonError.toTransport() = Error(
    code = code.takeIf { it.isNotBlank() },
    group = group.takeIf { it.isNotBlank() },
    field = field.takeIf { it.isNotBlank() },
    message = message.takeIf { it.isNotBlank() },
)