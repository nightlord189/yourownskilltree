package validation

import org.aburavov.yourownskilltree.backend.biz.validation.*
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.cor.Chain
import org.aburavov.yourownskilltree.backend.cor.Worker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidationTest {
    private fun makeChain(validationWorker: Worker<NodeContext>) = Chain<NodeContext>(
        workers = mutableListOf(validationWorker, ValidatorFinish())
    )

    @Nested
    inner class NameValidation {
        @Test
        fun `valid name`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    name = "name1"
                }
            }
            makeChain(Validator(::validateName)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid name`() {
            val context = NodeContext().apply {
                nodeRequest = Node()
            }
            makeChain(Validator(::validateName)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("name is empty") })
        }
    }

    @Nested
    inner class IdValidation {
        @Test
        fun `valid id`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    id = "id1"
                }
            }
            makeChain(Validator(::validateId)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid id`() {
            val context = NodeContext().apply {
                nodeRequest = Node()
            }
            makeChain(Validator(::validateId)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("id must be filled") })
        }

        @Test
        fun `valid id from request`() {
            val context = NodeContext().apply {
                nodeIdRequest = "id1"
            }
            makeChain(Validator(::validateIdRequest)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid id from request`() {
            val context = NodeContext()
            makeChain(Validator(::validateIdRequest)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("id must be filled") })
        }
    }

    @Nested
    inner class LockValidation {
        @Test
        fun `valid lock`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    lock = "lock"
                }
            }
            makeChain(Validator(::validateLock)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid lock`() {
            val context = NodeContext().apply {
                nodeRequest = Node()
            }
            makeChain(Validator(::validateLock)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("lock must be filled") })
        }

        @Test
        fun `valid lock from request`() {
            val context = NodeContext().apply {
                nodeLock = "lock1"
            }
            makeChain(Validator(::validateLockRequest)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid lock from request`() {
            val context = NodeContext()
            makeChain(Validator(::validateLockRequest)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("lock must be filled") })
        }
    }

    @Nested
    inner class FilterValidation {
        @Test
        fun `valid filter`() {
            val context = NodeContext().apply {
                nodeFilterRequest = NodeFilter(parentId = "parent1")
            }
            makeChain(Validator(::validateFilter)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid filter`() {
            val context = NodeContext().apply {
                nodeFilterRequest = NodeFilter()
            }
            makeChain(Validator(::validateFilter)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("parent_id or name_like must be filled") })
        }

        @Test
        fun `empty filter`() {
            val context = NodeContext()
            makeChain(Validator(::validateFilter)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("filter must be filled") })
        }
    }

    @Nested
    inner class BusinessDataValidation {
        @Test
        fun `valid business data`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    completionType = NodeCompletionType.TEST
                    questions = mutableListOf(
                        Question("txt", QuestionType.OPEN)
                    )
                }
            }
            makeChain(Validator(::validateBusiness)).run(context)
            assertTrue(context.errors.isEmpty())
        }

        @Test
        fun `invalid business data - percentage`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    completionType = NodeCompletionType.PERCENTAGE
                    progress = 150
                }
            }
            makeChain(Validator(::validateBusiness)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("progress must be equal to [0;100] with PERCENTAGE completion") })
        }

        @Test
        fun `invalid business data - test questions`() {
            val context = NodeContext().apply {
                nodeRequest = Node().apply {
                    completionType = NodeCompletionType.TEST
                }
            }
            makeChain(Validator(::validateBusiness)).run(context)
            assertTrue(context.errors.count() == 1)
            assertTrue(context.errors.any { it.message.contains("at least one question must be filled for TEST completion") })
        }
    }
}