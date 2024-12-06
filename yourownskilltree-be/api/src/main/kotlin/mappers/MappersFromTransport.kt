package org.aburavov.yourownskilltree.backend.api.mappers

import org.aburavov.yourownskilltree.backend.api.model.*
import org.aburavov.yourownskilltree.backend.common.model.NodeCommand
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.NodeStubs
import org.aburavov.yourownskilltree.backend.common.model.WorkMode
import org.aburavov.yourownskilltree.backend.common.model.QuestionType as DomainQuestionType
import org.aburavov.yourownskilltree.backend.common.model.Question as DomainQuestion
import org.aburavov.yourownskilltree.backend.common.model.Node as DomainNode
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus as DomainNodeStatus
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType as DomainNodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter as DomainNodeFilter

fun NodeContext.fromTransport(request: IRequest) = when (request) {
    is NodeCreateRequest -> fromTransport(request)
    is NodeReadRequest -> fromTransport(request)
    is NodeUpdateRequest -> fromTransport(request)
    is NodeDeleteRequest -> fromTransport(request)
    is NodeSearchRequest -> fromTransport(request)
    else -> throw Exception("unknown request class")
}

fun NodeContext.fromTransport(request: NodeCreateRequest) {
    command = NodeCommand.CREATE
    nodeRequest = request.node?.toDomain() ?: DomainNode()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

fun NodeContext.fromTransport(request: NodeReadRequest) {
    command = NodeCommand.READ
    nodeRequest = DomainNode().apply{id = request.id!!}
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

fun NodeContext.fromTransport(request: NodeUpdateRequest) {
    command = NodeCommand.UPDATE
    nodeRequest = request.node?.toDomain() ?: DomainNode()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

fun NodeContext.fromTransport(request: NodeDeleteRequest) {
    command = NodeCommand.DELETE
    nodeRequest = DomainNode().apply{id = request.id!!}
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

fun NodeContext.fromTransport(request: NodeSearchRequest) {
    command = NodeCommand.SEARCH
    nodeFilterRequest = request.filter?.toDomain()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun Debug?.transportToWorkMode(): WorkMode = when (this?.mode) {
    Debug.Mode.PROD -> WorkMode.PROD
    Debug.Mode.TEST -> WorkMode.TEST
    Debug.Mode.STUB -> WorkMode.STUB
    null -> WorkMode.PROD
}

private fun Debug?.transportToStubCase(): NodeStubs = when (this?.stub) {
    Debug.Stub.SUCCESS -> NodeStubs.SUCCESS
    Debug.Stub.NOT_FOUND -> NodeStubs.NOT_FOUND
    Debug.Stub.BAD_ID -> NodeStubs.BAD_ID
    Debug.Stub.CANNOT_DELETE -> NodeStubs.CANNOT_DELETE
    Debug.Stub.DB_ERROR -> NodeStubs.DB_ERROR
    null -> NodeStubs.NONE
}

fun NodeSearchRequestAllOfFilter.toDomain(): DomainNodeFilter {
    return DomainNodeFilter(
        parentId = parentId,
        nameLike = nameLike
    )
}

fun Question.toDomain(): DomainQuestion {
    return DomainQuestion(
        text = requireNotNull(text) { "Question text cannot be null" },
        type = when(requireNotNull(type) { "Question type cannot be null" }) {
            Question.Type.OPEN -> DomainQuestionType.OPEN
            Question.Type.CLOSED -> DomainQuestionType.CLOSED
        },
        answers = answers?.takeIf { it.isNotEmpty() },
        rightAnswer = rightAnswer
    )
}

fun Node.toDomain(): DomainNode {
    return DomainNode().apply {
        name = this@toDomain.name
        completionType = when(this@toDomain.completionType) {
            Node.CompletionType.BOOL -> DomainNodeCompletionType.BOOL
            Node.CompletionType.PERCENTAGE -> DomainNodeCompletionType.PERCENTAGE
            Node.CompletionType.TEST -> DomainNodeCompletionType.TEST
        }
        status = when(this@toDomain.status) {
            Node.Status.CLOSED -> DomainNodeStatus.CLOSED
            Node.Status.OPEN -> DomainNodeStatus.OPEN
            Node.Status.IN_PROGRESS -> DomainNodeStatus.IN_PROGRESS
            Node.Status.COMPLETED -> DomainNodeStatus.COMPLETED
        }
        description = this@toDomain.description
        parentIds = this@toDomain.parentIds ?: emptyList()
        progress = this@toDomain.progress
        questions = this@toDomain.questions?.map { it.toDomain() }
    }
}