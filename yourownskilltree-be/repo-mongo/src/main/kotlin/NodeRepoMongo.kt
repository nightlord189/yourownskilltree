package org.aburavov.yourownskilltree.backend.repo.mongo

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import repo.*
import java.util.*

class NodeRepoMongo (
    config: MongoConfig,
    collectionName: String = "nodes",
): MongoRepo<MongoNode>(config, collectionName, documentClass = MongoNode::class), IRepoNode {
    override suspend fun createNode(node: Node): DbNodeResponse {
        try {
            val dbEntity = MongoNode.fromCommon(node)
            dbEntity.mongoId = ObjectId()
            dbEntity.id = UUID.randomUUID().toString()
            dbEntity.lock = UUID.randomUUID().toString()
            collection.insertOne(dbEntity)
            return DbNodeResponse(dbEntity.toCommon())
        } catch (e: Exception) {
            return DbNodeResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun readNode(id: String): DbNodeResponse {
        try {
            val dbEntity = collection
                .find(Filters.eq("id", id)).firstOrNull()

            return if (dbEntity != null) {
                DbNodeResponse(dbEntity.toCommon())
            } else {
                DbNodeResponse(errors = listOf(CommonError(message = "not found")))
            }
        } catch (e: Exception) {
            return DbNodeResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun updateNode(node: Node): DbNodeResponse {
        try {
            val dbEntity = MongoNode.fromCommon(node)

            val existingEntity = collection
                .find(Filters.eq("id", dbEntity.id)).firstOrNull()

            if (existingEntity == null) {
                return DbNodeResponse(errors = listOf(CommonError(message = "not found")))
            }

            if (existingEntity.lock != node.lock) {
                return DbNodeResponse(errors = listOf(CommonError(message = "invalid lock")))
            }

            dbEntity.mongoId = existingEntity.mongoId ?: ObjectId()
            dbEntity.lock = UUID.randomUUID().toString() // Обновляем lock при каждом изменении

            val result = collection.replaceOne(
                Filters.and(
                    Filters.eq("_id", dbEntity.mongoId),
                ),
                dbEntity
            )

            return if (result.modifiedCount > 0) {
                DbNodeResponse(dbEntity.toCommon())
            } else {
                DbNodeResponse(errors = listOf(CommonError(message = "node not found or lock mismatch")))
            }
        } catch (e: Exception) {
            return DbNodeResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun deleteNode(id: String, lock: String): DbNodeResponse {
        try {
            val existingEntity = collection
                .find(Filters.eq("id", id)).firstOrNull()

            if (existingEntity == null) {
                return DbNodeResponse(errors = listOf(CommonError(message = "not found")))
            }

            if (existingEntity.lock != lock) {
                return DbNodeResponse(errors = listOf(CommonError(message = "invalid lock")))
            }

            collection.deleteOne(
                Filters.and(
                    Filters.eq("_id", existingEntity?.mongoId),
                )
            )

            return DbNodeResponse(existingEntity.toCommon() ?: Node())
        } catch (e: Exception) {
            return DbNodeResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }

    override suspend fun searchNode(filter: NodeFilter): DbNodesResponse {
        try {
            val filters = mutableListOf<Bson>()

            filter.nameLike?.let { nameLike ->
                filters.add(
                    Filters.regex("name", ".*$nameLike.*", "i")
                )
            }

            filter.parentId?.let { parentId ->
                filters.add(Filters.`in`("parentIds", parentId))
            }

            val query = if (filters.isNotEmpty()) {
                Filters.and(filters)
            } else {
                Filters.empty()
            }

            val nodes = collection.find(query).toList().map {  it.toCommon()}

            return DbNodesResponse(nodes)
        } catch (e: Exception) {
            return DbNodesResponse(errors = listOf(CommonError(message = e.message?:"")))
        }
    }
}