import org.aburavov.yourownskilltree.backend.common.repo.IRepoNode
import org.junit.jupiter.api.BeforeEach

class NodeRepoInMemoryTest: NodeRepoTest() {
    override lateinit var repo: IRepoNode

    @BeforeEach
    fun setUp() {
        repo = NodeRepoInMemory()
    }
}