package org.aburavov.yourownskilltree.backend.repo.mongo

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.aburavov.yourownskilltree.backend.common.model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.repo.DbAccessEntityResponse
import org.aburavov.yourownskilltree.backend.common.repo.DbResponse
import org.aburavov.yourownskilltree.backend.common.repo.IRepoAccessEntity
import org.bson.types.ObjectId

class AccessEntityRepoMongo (
    config: MongoConfig,
    collectionName: String = "access_entities"
): MongoRepo<MongoAccessEntity>(config, collectionName, MongoAccessEntity::class), IRepoAccessEntity {

    override suspend fun put(item: AccessEntity): DbResponse<AccessEntity> {
        try {
            val newDbEntity = MongoAccessEntity.fromCommon(item)

            val existingDbEntity = collection
                .find(Filters.and(
                    Filters.eq("userId", item.userId),
                    Filters.eq("resourceId", item.resourceId)
                )).firstOrNull()

            if (existingDbEntity != null) {
                newDbEntity.mongoId = existingDbEntity.mongoId
                collection.replaceOne(
                    Filters.eq("_id", existingDbEntity.mongoId),
                    newDbEntity
                )
            } else {
                newDbEntity.mongoId = ObjectId()
                collection.insertOne(newDbEntity)
            }

            return DbAccessEntityResponse(data = newDbEntity.toCommon())
        } catch (e: Exception) {
            return DbAccessEntityResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun read(userId: String, resourceId: String): DbResponse<AccessEntity> {
        try {
            val dbEntity = collection
                .find(Filters.and(
                    Filters.eq("userId", userId),
                    Filters.eq("resourceId", resourceId)
                )).firstOrNull()

            return if (dbEntity != null) {
                DbAccessEntityResponse(dbEntity.toCommon())
            } else {
                DbAccessEntityResponse(errors = listOf(CommonError(message = "not found")))
            }
        } catch (e: Exception) {
            return DbAccessEntityResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun delete(userId: String, resourceId: String): DbResponse<AccessEntity> {
        try {
            collection.deleteOne(
                Filters.and(
                    Filters.eq("userId", userId),
                    Filters.eq("resourceId", resourceId)
                )
            )

            return DbAccessEntityResponse()
        } catch (e: Exception) {
            return DbAccessEntityResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }
}