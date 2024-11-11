package org.aburavov.yourownskilltree.backend.common.mapper

import com.yourownskilltree.backend.api.model.Node as TransportNode
import com.yourownskilltree.backend.api.model.Question as TransportQuestion
import org.aburavov.yourownskilltree.backend.common.model.Node as DomainNode
import org.aburavov.yourownskilltree.backend.common.model.NodeResponse as DomainNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.aburavov.yourownskilltree.backend.common.model.Question
import org.aburavov.yourownskilltree.backend.common.model.QuestionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class NodeMapperTest {

    @Test
    fun `should map domain to transport node correctly with all fields`() {
        // given
        val domainNode = DomainNode().apply {
            name = "Docker Basics"
            completionType = NodeCompletionType.TEST
            status = NodeStatus.IN_PROGRESS
            description = "Learn Docker fundamentals"
            parentIds = listOf("containers-101")
            progress = 75
            questions = listOf(
                Question(
                    text = "What is Docker?",
                    type = QuestionType.OPEN,
                    rightAnswer = "A containerization platform"
                )
            )
        }

        // when
        val transportNode = NodeMapper.toTransport(domainNode)

        // then
        assertThat(transportNode.name).isEqualTo(domainNode.name)
        assertThat(transportNode.completionType).isEqualTo(TransportNode.CompletionType.TEST)
        assertThat(transportNode.status).isEqualTo(TransportNode.Status.IN_PROGRESS)
        assertThat(transportNode.description).isEqualTo(domainNode.description)
        assertThat(transportNode.parentIds).isEqualTo(domainNode.parentIds)
        assertThat(transportNode.progress).isEqualTo(domainNode.progress)
        assertThat(transportNode.questions).hasSize(1)
        assertThat(transportNode.questions?.first()?.text).isEqualTo("What is Docker?")
    }

    @Test
    fun `should handle null optional fields correctly`() {
        // given
        val transportNode = TransportNode(
            name = "Minimal Node",
            completionType = TransportNode.CompletionType.BOOL,
            status = TransportNode.Status.CLOSED
        )

        // when
        val domainNode = NodeMapper.toDomain(transportNode)

        // then
        assertThat(domainNode.description).isNull()
        assertThat(domainNode.parentIds).isEmpty()
        assertThat(domainNode.progress).isNull()
        assertThat(domainNode.questions).isNull()
    }

    @ParameterizedTest
    @EnumSource(NodeCompletionType::class)
    fun `should map all completion types correctly`(completionType: NodeCompletionType) {
        // given
        val domainNode = DomainNode().apply {
            name = "Test Node"
            this.completionType = completionType
            status = NodeStatus.CLOSED
        }

        // when
        val transportNode = NodeMapper.toTransport(domainNode)
        val mappedBackDomainNode = NodeMapper.toDomain(transportNode)

        // then
        assertThat(mappedBackDomainNode.completionType).isEqualTo(completionType)
    }

    @ParameterizedTest
    @EnumSource(NodeStatus::class)
    fun `should map all status types correctly`(status: NodeStatus) {
        // given
        val domainNode = DomainNode().apply {
            name = "Test Node"
            completionType = NodeCompletionType.BOOL
            this.status = status
        }

        // when
        val transportNode = NodeMapper.toTransport(domainNode)
        val mappedBackDomainNode = NodeMapper.toDomain(transportNode)

        // then
        assertThat(mappedBackDomainNode.status).isEqualTo(status)
    }
}