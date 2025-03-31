package org.aburavov.yourownskilltree.backend.stubs

import org.aburavov.yourownskilltree.backend.common.model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import org.aburavov.yourownskilltree.backend.common.repo.DbAccessEntityResponse
import org.aburavov.yourownskilltree.backend.common.repo.DbResponse
import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import java.time.LocalDateTime

class AccessEntityRepoStub: IRepoAccessEntity {
    override suspend fun put(item: AccessEntity): DbResponse<AccessEntity> {
        return DbAccessEntityResponse(data = item)
    }

    override suspend fun read(userId: String, resourceId: String): DbResponse<AccessEntity> {
        val result = AccessEntity(
            userId = userId,
            resourceId =  resourceId,
            accessLevel = NodeAccessLevel.FULL_ACCESS,
        )
        return DbAccessEntityResponse(data = result)
    }

    override suspend fun delete(userId: String, resourceId: String): DbResponse<AccessEntity> {
        return DbAccessEntityResponse()
    }
}