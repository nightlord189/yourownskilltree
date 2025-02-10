import model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import repo.*

class AccessEntityRepoInMemory (existingItems: MutableList<AccessEntity> = mutableListOf()): IRepoAccessEntity {
    private val items: MutableList<AccessEntity> = existingItems

    override suspend fun read(userId: String, resourceId: String): DbAccessEntityResponse {
        val dbEntity = items.firstOrNull() { it.userId == userId && it.resourceId == resourceId }
        if (dbEntity == null) {
            return DbAccessEntityResponse(errors = listOf(CommonError(message = "not found")))
        }
        return DbAccessEntityResponse(dbEntity)
    }

    override suspend fun put(item: AccessEntity): DbAccessEntityResponse {
        val index = items.indexOfFirst { it.userId == item.userId && it.resourceId == item.resourceId }
        if (index == -1) {
            items.add(item)
        } else {
            items[index] = item
        }
        return DbAccessEntityResponse(item)
    }

    override suspend fun delete(userId: String, resourceId: String): DbAccessEntityResponse {
        val dbEntity = items.firstOrNull() { it.userId == userId && it.resourceId == resourceId }
        if (dbEntity == null) {
            return DbAccessEntityResponse(errors = listOf(CommonError(message = "not found")))
        }
        items.remove(dbEntity)
        return DbAccessEntityResponse(dbEntity)
    }
}