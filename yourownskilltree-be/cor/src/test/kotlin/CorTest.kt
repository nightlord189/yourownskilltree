package org.aburavov.yourownskilltree.backend.cor

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ChainTest {
    // Тестовый контекст
    data class TestContext(
        var value: Int = 0,
        val executedWorkers: MutableList<String> = mutableListOf()
    )

    // Базовый тестовый воркер
    class TestWorker(
        private val name: String,
        private val shouldHandle: Boolean = true,
        private val shouldContinue: Boolean = true
    ) : Worker<TestContext>() {
        override fun on(context: TestContext): Boolean = shouldHandle

        override fun handle(context: TestContext): Boolean {
            context.executedWorkers.add(name)
            return shouldContinue
        }
    }

    @Test
    fun `empty chain should do nothing`() {
        // Arrange
        val ctx = TestContext()
        val chain = Chain<TestContext>(mutableListOf())

        // Act
        chain.run(ctx)

        // Assert
        assertTrue(ctx.executedWorkers.isEmpty())
    }

    @Test
    fun `chain should execute all workers when all return true`() {
        // Arrange
        val ctx = TestContext()
        val chain = Chain(
            mutableListOf(
                TestWorker("worker1"),
                TestWorker("worker2"),
                TestWorker("worker3")
            )
        )

        // Act
        chain.run(ctx)

        // Assert
        assertEquals(
            listOf("worker1", "worker2", "worker3"),
            ctx.executedWorkers
        )
    }

    @Test
    fun `chain should stop when worker returns false`() {
        // Arrange
        val ctx = TestContext()
        val chain = Chain(
            mutableListOf(
                TestWorker("worker1"),
                TestWorker("worker2", shouldContinue = false),
                TestWorker("worker3")
            )
        )

        // Act
        chain.run(ctx)

        // Assert
        assertEquals(
            listOf("worker1", "worker2"),
            ctx.executedWorkers
        )
    }

    @Test
    fun `chain should skip workers when on returns false`() {
        // Arrange
        val ctx = TestContext()
        val chain = Chain(
            mutableListOf(
                TestWorker("worker1"),
                TestWorker("worker2", shouldHandle = false),
                TestWorker("worker3")
            )
        )

        // Act
        chain.run(ctx)

        // Assert
        assertEquals(
            listOf("worker1", "worker3"),
            ctx.executedWorkers
        )
    }

    @Test
    fun `chain should handle real use case`() {
        // Arrange
        data class NodeContext(
            var value: String = "",
            val errors: MutableList<String> = mutableListOf()
        )

        class ValidatorWorker : Worker<NodeContext>() {
            override fun on(context: NodeContext) = true

            override fun handle(context: NodeContext): Boolean {
                if (context.value.isEmpty()) {
                    context.errors.add("Value cannot be empty")
                    return false
                }
                return true
            }
        }

        class ProcessorWorker : Worker<NodeContext>() {
            override fun on(context: NodeContext) = context.errors.isEmpty()

            override fun handle(context: NodeContext): Boolean {
                context.value = context.value.uppercase()
                return true
            }
        }

        // Test with valid input
        val validContext = NodeContext("test")
        val validChain = Chain(mutableListOf(ValidatorWorker(), ProcessorWorker()))
        validChain.run(validContext)

        assertEquals("TEST", validContext.value)
        assertTrue(validContext.errors.isEmpty())

        // Test with invalid input
        val invalidContext = NodeContext("")
        val invalidChain = Chain(mutableListOf(ValidatorWorker(), ProcessorWorker()))
        invalidChain.run(invalidContext)

        assertEquals("", invalidContext.value)
        assertEquals(listOf("Value cannot be empty"), invalidContext.errors)
    }
}