package org.aburavov.yourownskilltree.backend.common.mapper

import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toDomain
import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toTransport
import com.yourownskilltree.backend.api.model.Node as TransportNode
import com.yourownskilltree.backend.api.model.NodeResponse as TransportNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.Node as DomainNode
import org.aburavov.yourownskilltree.backend.common.model.NodeResponse as DomainNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType as DomainCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus as DomainStatus

object NodeMapper {

    fun toTransport(domain: DomainNode): TransportNode {
        return TransportNode(
            name = domain.name,
            completionType = domain.completionType.toTransport(),
            status = domain.status.toTransport(),
            description = domain.description,
            parentIds = domain.parentIds,
            progress = domain.progress,
            questions = domain.questions?.toTransport()
        )
    }

    fun toDomain(transport: TransportNode): DomainNode {
        return DomainNode().apply {
            name = transport.name
            completionType = transport.completionType.toDomain()
            status = transport.status.toDomain()
            description = transport.description
            parentIds = transport.parentIds ?: emptyList()
            progress = transport.progress
            questions = transport.questions?.toDomain()
        }
    }

    private fun DomainCompletionType.toTransport(): TransportNode.CompletionType = when (this) {
        DomainCompletionType.BOOL -> TransportNode.CompletionType.BOOL
        DomainCompletionType.PERCENTAGE -> TransportNode.CompletionType.PERCENTAGE
        DomainCompletionType.TEST -> TransportNode.CompletionType.TEST
    }

    private fun TransportNode.CompletionType.toDomain(): DomainCompletionType = when (this) {
        TransportNode.CompletionType.BOOL -> DomainCompletionType.BOOL
        TransportNode.CompletionType.PERCENTAGE -> DomainCompletionType.PERCENTAGE
        TransportNode.CompletionType.TEST -> DomainCompletionType.TEST
    }

    private fun DomainStatus.toTransport(): TransportNode.Status = when (this) {
        DomainStatus.CLOSED -> TransportNode.Status.CLOSED
        DomainStatus.OPEN -> TransportNode.Status.OPEN
        DomainStatus.IN_PROGRESS -> TransportNode.Status.IN_PROGRESS
        DomainStatus.COMPLETED -> TransportNode.Status.COMPLETED
    }

    private fun TransportNode.Status.toDomain(): DomainStatus = when (this) {
        TransportNode.Status.CLOSED -> DomainStatus.CLOSED
        TransportNode.Status.OPEN -> DomainStatus.OPEN
        TransportNode.Status.IN_PROGRESS -> DomainStatus.IN_PROGRESS
        TransportNode.Status.COMPLETED -> DomainStatus.COMPLETED
    }
}