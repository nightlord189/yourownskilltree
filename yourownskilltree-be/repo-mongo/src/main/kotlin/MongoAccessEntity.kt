package org.aburavov.yourownskilltree.backend.repo.mongo

import model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class MongoAccessEntity (
    @BsonId
    @BsonProperty("_id")
    var mongoId: ObjectId = ObjectId(),
    val userId: String,
    val resourceId: String,
    val accessLevel: NodeAccessLevel,
    val grantedAt: LocalDateTime
) {
    companion object {
        fun fromCommon(item: AccessEntity): MongoAccessEntity = MongoAccessEntity(
            userId = item.userId,
            resourceId =  item.resourceId,
            accessLevel = item.accessLevel,
            grantedAt =  item.grantedAt,
        )
    }

    fun toCommon(): AccessEntity = AccessEntity(
        userId = userId,
        resourceId =  resourceId,
        accessLevel = accessLevel,
        grantedAt =  grantedAt,
    )
}