import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import org.junit.jupiter.api.BeforeEach

class AccessEntityRepoInMemoryTest: AccessEntityRepoTest() {
    override lateinit var repo: IRepoAccessEntity

    @BeforeEach
    fun setUp() {
        repo = AccessEntityRepoInMemory()
    }
}