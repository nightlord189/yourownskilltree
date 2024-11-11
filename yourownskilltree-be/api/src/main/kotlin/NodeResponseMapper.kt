package org.aburavov.yourownskilltree.backend.common.mapper

import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toDomain
import org.aburavov.yourownskilltree.backend.common.mapper.QuestionMapper.toTransport
import com.yourownskilltree.backend.api.model.NodeResponse as TransportNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.NodeResponse as DomainNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType as DomainCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus as DomainStatus

object NodeResponseMapper {

    fun toTransport(domain: DomainNodeResponse): TransportNodeResponse {
        return TransportNodeResponse(
            id = domain.id,
            name = domain.name,
            completionType = domain.completionType.toTransport(),
            status = domain.status.toTransport(),
            description = domain.description,
            parentIds = domain.parentIds.takeIf { it.isNotEmpty() },
            progress = domain.progress,
            questions = domain.questions?.toTransport()
        )
    }

    fun toDomain(transport: TransportNodeResponse): DomainNodeResponse {
        return DomainNodeResponse(transport.id).apply {
            name = transport.name
            completionType = transport.completionType.toDomain()
            status = transport.status.toDomain()
            description = transport.description
            parentIds = transport.parentIds ?: emptyList()
            progress = transport.progress
            questions = transport.questions?.toDomain()
        }
    }

    private fun DomainCompletionType.toTransport(): TransportNodeResponse.CompletionType = when (this) {
        DomainCompletionType.BOOL -> TransportNodeResponse.CompletionType.BOOL
        DomainCompletionType.PERCENTAGE -> TransportNodeResponse.CompletionType.PERCENTAGE
        DomainCompletionType.TEST -> TransportNodeResponse.CompletionType.TEST
    }

    private fun TransportNodeResponse.CompletionType.toDomain(): DomainCompletionType = when (this) {
        TransportNodeResponse.CompletionType.BOOL -> DomainCompletionType.BOOL
        TransportNodeResponse.CompletionType.PERCENTAGE -> DomainCompletionType.PERCENTAGE
        TransportNodeResponse.CompletionType.TEST -> DomainCompletionType.TEST
    }

    private fun DomainStatus.toTransport(): TransportNodeResponse.Status = when (this) {
        DomainStatus.CLOSED -> TransportNodeResponse.Status.CLOSED
        DomainStatus.OPEN -> TransportNodeResponse.Status.OPEN
        DomainStatus.IN_PROGRESS -> TransportNodeResponse.Status.IN_PROGRESS
        DomainStatus.COMPLETED -> TransportNodeResponse.Status.COMPLETED
    }

    private fun TransportNodeResponse.Status.toDomain(): DomainStatus = when (this) {
        TransportNodeResponse.Status.CLOSED -> DomainStatus.CLOSED
        TransportNodeResponse.Status.OPEN -> DomainStatus.OPEN
        TransportNodeResponse.Status.IN_PROGRESS -> DomainStatus.IN_PROGRESS
        TransportNodeResponse.Status.COMPLETED -> DomainStatus.COMPLETED
    }
}