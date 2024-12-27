import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import repo.IRepoNode

@Testcontainers
class NodeRepoMongoTest:  NodeRepoTest() {
    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:6.0"))
            .apply { start() }

        private val logger = KotlinLogging.logger {}
    }

    override lateinit var repo: IRepoNode

    @BeforeEach
    fun setUp() {
        repo = NodeRepoMongo(
            config = MongoConfig(
                host = mongoDBContainer.host,
                port = mongoDBContainer.getMappedPort(27017),
                database = "test_db"
            ),
        )
    }

    @AfterEach
    fun tearDown() = runBlocking {
        (repo as NodeRepoMongo).close()
    }
}