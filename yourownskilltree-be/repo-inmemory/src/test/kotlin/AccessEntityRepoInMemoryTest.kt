import org.junit.jupiter.api.BeforeEach
import repo.IRepoAccessEntity

class AccessEntityRepoInMemoryTest: AccessEntityRepoTest() {
    override lateinit var repo: IRepoAccessEntity

    @BeforeEach
    fun setUp() {
        repo = AccessEntityRepoInMemory()
    }
}