package repo

import model.AccessEntity

interface IRepoAccessEntity {
    suspend fun put(item: AccessEntity): DbResponse<AccessEntity>
    suspend fun read(userId: String, resourceId: String): DbResponse<AccessEntity>
    suspend fun delete(userId: String, resourceId: String): DbResponse<AccessEntity>
}
