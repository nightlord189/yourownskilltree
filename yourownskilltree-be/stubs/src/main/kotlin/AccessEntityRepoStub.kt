package org.aburavov.yourownskilltree.backend.stubs

import model.AccessEntity
import permissions.NodeAccessLevel
import repo.DbAccessEntityResponse
import repo.DbResponse
import repo.IRepoAccessEntity
import java.time.LocalDateTime
import java.util.*

class AccessEntityRepoStub: IRepoAccessEntity {
    override suspend fun put(item: AccessEntity): DbResponse<AccessEntity> {
        return DbAccessEntityResponse(data = item)
    }

    override suspend fun read(userId: String, resourceId: String): DbResponse<AccessEntity> {
        val result = AccessEntity(
            userId = userId,
            resourceId =  resourceId,
            accessLevel = NodeAccessLevel.FULL_ACCESS,
            grantedAt = LocalDateTime.now(),
        )
        return DbAccessEntityResponse(data = result)
    }

    override suspend fun delete(userId: String, resourceId: String): DbResponse<AccessEntity> {
        return DbAccessEntityResponse()
    }


}