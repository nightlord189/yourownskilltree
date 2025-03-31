package org.aburavov.yourownskilltree.backend.common.repo

import org.aburavov.yourownskilltree.backend.common.model.AccessEntity

interface IRepoAccessEntity {
    suspend fun put(item: AccessEntity): DbResponse<AccessEntity>
    suspend fun read(userId: String, resourceId: String): DbResponse<AccessEntity>
    suspend fun delete(userId: String, resourceId: String): DbResponse<AccessEntity>
}
