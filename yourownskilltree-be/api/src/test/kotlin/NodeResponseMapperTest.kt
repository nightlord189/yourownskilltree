package org.aburavov.yourownskilltree.backend.common.mapper

import com.yourownskilltree.backend.api.model.NodeResponse as TransportNodeResponse
import com.yourownskilltree.backend.api.model.Question as TransportQuestion
import org.aburavov.yourownskilltree.backend.common.model.NodeResponse as DomainNodeResponse
import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.aburavov.yourownskilltree.backend.common.model.Question
import org.aburavov.yourownskilltree.backend.common.model.QuestionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class NodeResponseMapperTest {

    @Test
    fun `should map domain to transport node response correctly with all fields`() {
        // given
        val domainNodeResponse = DomainNodeResponse("node-123").apply {
            name = "Docker Advanced"
            completionType = NodeCompletionType.TEST
            status = NodeStatus.IN_PROGRESS
            description = "Advanced Docker concepts"
            parentIds = listOf("docker-basics")
            progress = 85
            questions = listOf(
                Question(
                    text = "Explain Docker networking",
                    type = QuestionType.OPEN,
                    rightAnswer = "Docker networking enables communication between containers"
                )
            )
        }

        // when
        val transportNodeResponse = NodeResponseMapper.toTransport(domainNodeResponse)

        // then
        assertThat(transportNodeResponse.id).isEqualTo(domainNodeResponse.id)
        assertThat(transportNodeResponse.name).isEqualTo(domainNodeResponse.name)
        assertThat(transportNodeResponse.completionType).isEqualTo(TransportNodeResponse.CompletionType.TEST)
        assertThat(transportNodeResponse.status).isEqualTo(TransportNodeResponse.Status.IN_PROGRESS)
        assertThat(transportNodeResponse.description).isEqualTo(domainNodeResponse.description)
        assertThat(transportNodeResponse.parentIds).isEqualTo(domainNodeResponse.parentIds)
        assertThat(transportNodeResponse.progress).isEqualTo(domainNodeResponse.progress)
        assertThat(transportNodeResponse.questions).hasSize(1)
        assertThat(transportNodeResponse.questions?.first()?.text).isEqualTo("Explain Docker networking")
    }

    @Test
    fun `should map transport to domain node response correctly`() {
        // given
        val transportNodeResponse = TransportNodeResponse(
            id = "node-456",
            name = "Kubernetes Advanced",
            completionType = TransportNodeResponse.CompletionType.PERCENTAGE,
            status = TransportNodeResponse.Status.OPEN,
            description = "Advanced K8s concepts",
            parentIds = listOf("k8s-basics"),
            progress = 40,
            questions = listOf(
                TransportQuestion(
                    text = "Explain K8s Services",
                    type = TransportQuestion.Type.OPEN
                )
            )
        )

        // when
        val domainNodeResponse = NodeResponseMapper.toDomain(transportNodeResponse)

        // then
        assertThat(domainNodeResponse.id).isEqualTo(transportNodeResponse.id)
        assertThat(domainNodeResponse.name).isEqualTo(transportNodeResponse.name)
        assertThat(domainNodeResponse.completionType).isEqualTo(NodeCompletionType.PERCENTAGE)
        assertThat(domainNodeResponse.status).isEqualTo(NodeStatus.OPEN)
        assertThat(domainNodeResponse.description).isEqualTo(transportNodeResponse.description)
        assertThat(domainNodeResponse.parentIds).isEqualTo(transportNodeResponse.parentIds)
        assertThat(domainNodeResponse.progress).isEqualTo(transportNodeResponse.progress)
        assertThat(domainNodeResponse.questions).hasSize(1)
    }

    @Test
    fun `should handle empty parent ids correctly`() {
        // given
        val domainNodeResponse = DomainNodeResponse("node-789").apply {
            name = "Empty Parents"
            completionType = NodeCompletionType.BOOL
            status = NodeStatus.CLOSED
            parentIds = emptyList()
        }

        // when
        val transportNodeResponse = NodeResponseMapper.toTransport(domainNodeResponse)

        // then
        assertThat(transportNodeResponse.parentIds).isNull()

        // and when mapping back
        val mappedBackDomain = NodeResponseMapper.toDomain(transportNodeResponse)

        // then
        assertThat(mappedBackDomain.parentIds).isEmpty()
    }

    @ParameterizedTest
    @EnumSource(NodeCompletionType::class)
    fun `should map all completion types correctly`(completionType: NodeCompletionType) {
        // given
        val domainNodeResponse = DomainNodeResponse("test-id").apply {
            name = "Test Node"
            this.completionType = completionType
            status = NodeStatus.CLOSED
        }

        // when
        val transportNodeResponse = NodeResponseMapper.toTransport(domainNodeResponse)
        val mappedBackDomainResponse = NodeResponseMapper.toDomain(transportNodeResponse)

        // then
        assertThat(mappedBackDomainResponse.completionType).isEqualTo(completionType)
    }

    @ParameterizedTest
    @EnumSource(NodeStatus::class)
    fun `should map all status types correctly`(status: NodeStatus) {
        // given
        val domainNodeResponse = DomainNodeResponse("test-id").apply {
            name = "Test Node"
            completionType = NodeCompletionType.BOOL
            this.status = status
        }

        // when
        val transportNodeResponse = NodeResponseMapper.toTransport(domainNodeResponse)
        val mappedBackDomainResponse = NodeResponseMapper.toDomain(transportNodeResponse)

        // then
        assertThat(mappedBackDomainResponse.status).isEqualTo(status)
    }

    @Test
    fun `should handle null optional fields correctly`() {
        // given
        val transportNodeResponse = TransportNodeResponse(
            id = "minimal-id",
            name = "Minimal Node",
            completionType = TransportNodeResponse.CompletionType.BOOL,
            status = TransportNodeResponse.Status.CLOSED
        )

        // when
        val domainNodeResponse = NodeResponseMapper.toDomain(transportNodeResponse)

        // then
        assertThat(domainNodeResponse.description).isNull()
        assertThat(domainNodeResponse.parentIds).isEmpty()
        assertThat(domainNodeResponse.progress).isNull()
        assertThat(domainNodeResponse.questions).isNull()
    }
}