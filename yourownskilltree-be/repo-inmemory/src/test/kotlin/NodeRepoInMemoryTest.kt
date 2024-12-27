import org.junit.jupiter.api.BeforeEach
import repo.IRepoNode

class NodeRepoInMemoryTest: NodeRepoTest() {
    override lateinit var repo: IRepoNode

    @BeforeEach
    fun setUp() {
        repo = NodeRepoInMemory()
    }
}