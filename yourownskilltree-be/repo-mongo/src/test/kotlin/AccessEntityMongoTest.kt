import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import org.aburavov.yourownskilltree.backend.repo.mongo.AccessEntityRepoMongo
import org.aburavov.yourownskilltree.backend.repo.mongo.MongoConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class AccessEntityMongoTest: AccessEntityRepoTest() {
    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:6.0"))
            .apply { start() }

        private val logger = KotlinLogging.logger {}
    }

    override lateinit var repo: IRepoAccessEntity

    @BeforeEach
    fun setUp() {
        repo = AccessEntityRepoMongo(
            config = MongoConfig(
                host = mongoDBContainer.host,
                port = mongoDBContainer.getMappedPort(27017),
                database = "test_db"
            ),
        )
    }

    @AfterEach
    fun tearDown() = runBlocking {
        (repo as AccessEntityRepoMongo).close()
    }
}