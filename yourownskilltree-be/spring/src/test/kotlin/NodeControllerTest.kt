package org.aburavov.yourownskilltree.backend.spring.controllers

import org.aburavov.yourownskilltree.backend.api.model.*
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@AutoConfigureWebTestClient
@WebFluxTest(NodeController::class)
class NodeControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var nodeProcessor: NodeProcessor

    @Test
    fun testCreate() {
        val request = NodeCreateRequest(
            requestType = "create",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            node = Node(
                id = "",
                name = "node1",
                description = "descr1",
                completionType = Node.CompletionType.BOOL,
                status = Node.Status.OPEN,
            )
        )

        testEndpoint<NodeCreateRequest, NodeCreateResponse>("/node/create", request) { response ->
            assert(response != null)
            assert(response?.result == ResponseResult.SUCCESS)
        }
    }

    @Test
    fun testSearch() {
        val request = NodeSearchRequest(
            requestType = "search",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            filter = NodeSearchRequestAllOfFilter(nameLike = "test")
        )

        testEndpoint<NodeSearchRequest, NodeSearchResponse>("/node/search", request) { response ->
            assert(response != null)
            assert(response?.result == ResponseResult.SUCCESS)
        }
    }

    @Test
    fun testRead() {
        val request = NodeReadRequest(
            requestType = "read",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            id = "201",
        )

        testEndpoint<NodeReadRequest, NodeReadResponse>("/node/read", request) { response ->
            assert(response != null)
            assert(response?.result == ResponseResult.SUCCESS)
        }
    }

    @Test
    fun testUpdate() {
        val request = NodeUpdateRequest(
            requestType = "update",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            node = Node(
                id = "111",
                name = "node1",
                description = "descr1",
                completionType = Node.CompletionType.BOOL,
                status = Node.Status.OPEN,
            )
        )

        testEndpoint<NodeUpdateRequest, NodeUpdateResponse>("/node/update", request) { response ->
            assert(response != null)
            assert(response?.result == ResponseResult.SUCCESS)
        }
    }

    @Test
    fun testDelete() {
        val request = NodeDeleteRequest(
            requestType = "read",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            id = "201",
        )

        testEndpoint<NodeDeleteRequest, NodeDeleteResponse>("/node/delete", request) { response ->
            assert(response != null)
            assert(response?.result == ResponseResult.SUCCESS)
        }
    }

    private inline fun <reified REQ : IRequest, reified RESP : IResponse> testEndpoint(
        uri: String,
        request: REQ,
        crossinline responseValidator: (RESP?) -> Unit
    ) {
        webTestClient.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<RESP>()
            .consumeWith { response ->
                responseValidator(response.responseBody)
            }
    }
}