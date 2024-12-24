import org.junit.jupiter.api.Test
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.aburavov.yourownskilltree.backend.api.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.GetResponse
import org.aburavov.yourownskilltree.backend.common.model.Node as commonNode
import org.aburavov.yourownskilltree.rabbit.config.RabbitConfig
import org.aburavov.yourownskilltree.rabbit.controllers.RabbitController
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@Testcontainers
class RabbitControllerTest {
    companion object {
        @Container
        val rabbitMQ = RabbitMQContainer("rabbitmq:3-management")
    }

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    private lateinit var controller: RabbitController
    private lateinit var testConfig: RabbitConfig
    private lateinit var factory: ConnectionFactory
    private lateinit var connection:Connection
    private lateinit var channel:Channel

    @BeforeEach
    fun setup() {
        testConfig = RabbitConfig(
            host = rabbitMQ.host,
            port = rabbitMQ.amqpPort,
            username = "guest",
            password = "guest",
            inputQueue = "test-input",
            outputQueue = "test-output"
        )
        controller = RabbitController(testConfig)
        controller.startProcessing()

        factory = ConnectionFactory().apply {
            host = testConfig.host
            port = testConfig.port
            username = testConfig.username
            password = testConfig.password
        }

        connection = factory.newConnection()
        channel = connection.createChannel()
    }

    @AfterEach
    fun teardown() {
        channel.close()
        connection.close()
        controller.close()
    }

    @Test
    fun testNodeCreate() {
        val request = NodeCreateRequest(
            requestType = "create",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            node = Node(
                id = "",
                name = "test node",
                description = "test description",
                completionType = Node.CompletionType.BOOL,
                status = Node.Status.OPEN
            )
        )

        publishToChannel(request)

        val response = readFromChannel()
        assert(response != null)

        val resp = objectMapper.readValue(response?.body, NodeCreateResponse::class.java)
        assert(resp.responseType == request.requestType)
        assert(resp.result == ResponseResult.SUCCESS)
        assert(resp.node?.name == request.node?.name)
        assert(resp.node?.description == request.node?.description)
        assert(resp.node?.id?.isNotEmpty() == true)
    }

    @Test
    fun testNodeUpdate() {
        val request = NodeUpdateRequest(
            requestType = "update",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            node = Node(
                id = "111",
                name = "newName",
                description = "test description",
                completionType = Node.CompletionType.BOOL,
                status = Node.Status.OPEN,
                lock = "lock1",
            )
        )

        publishToChannel(request)

        val response = readFromChannel()
        assert(response != null)

        val resp = objectMapper.readValue(response?.body, NodeUpdateResponse::class.java)
        assert(resp.responseType == request.requestType)
        assert(resp.result == ResponseResult.SUCCESS)
        assert(resp.node?.id == request.node?.id)
        assert(resp.node?.name == request.node?.name)
        assert(resp.node?.description == request.node?.description)
    }

    @Test
    fun testNodeRead() {
        val request = NodeReadRequest(
            requestType = "read",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            id = "111"
        )

        publishToChannel(request)

        val response = readFromChannel()
        assert(response != null)

        val resp = objectMapper.readValue(response?.body, NodeReadResponse::class.java)
        assert(resp.responseType == request.requestType)
        assert(resp.result == ResponseResult.SUCCESS)
        resp.node?.name?.isNotEmpty()?.let { assert(it) }
    }

    @Test
    fun testNodeSearch() {
        val request = NodeSearchRequest(
            requestType = "search",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            filter = NodeSearchRequestAllOfFilter(
                nameLike = "check"
            )
        )

        publishToChannel(request)

        val response = readFromChannel()
        assert(response != null)

        val resp = objectMapper.readValue(response?.body, NodeSearchResponse::class.java)
        assert(resp.responseType == request.requestType)
        assert(resp.result == ResponseResult.SUCCESS)
        assert(resp.nodes?.count() == 1)
    }

    @Test
    fun testNodeDelete() {
        val existingNode = commonNode().apply {
            id = "115"
            name = "nodeToDelete"
        }

        val request = NodeDeleteRequest(
            requestType = "delete",
            debug = Debug(mode = Debug.Mode.STUB, stub = Debug.Stub.SUCCESS),
            id = existingNode.id,
            lock = "lock1",
        )

        publishToChannel(request)

        val response = readFromChannel()
        assert(response != null)

        val resp = objectMapper.readValue(response?.body, NodeDeleteResponse::class.java)
        assert(resp.responseType == request.requestType)
        assert(resp.result == ResponseResult.SUCCESS)
    }



    private fun publishToChannel(request: Any) {
        channel.basicPublish(
            "",
            testConfig.inputQueue,
            null,
            objectMapper.writeValueAsString(request).toByteArray()
        )
    }

    private fun readFromChannel():GetResponse? {
        var response: GetResponse? = null
        val timeout = System.currentTimeMillis() + 5000 // 5 секунд таймаут

        while (response == null && System.currentTimeMillis() < timeout) {
            response = channel.basicGet(testConfig.outputQueue, true)
            if (response == null) Thread.sleep(100)
        }

        return response
    }
}