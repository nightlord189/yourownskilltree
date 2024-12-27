package org.aburavov.yourownskilltree.backend.repo.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import repo.*
import java.util.*

class NodeRepoMongo (
    config: MongoConfig,
    collectionName: String = "nodes"
): IRepoNode {
    private val logger = KotlinLogging.logger {}

    private lateinit var client: MongoClient
    private lateinit var collection: MongoCollection<MongoNode>

    init {
        println("DEBUG: Initializing MongoDB repository")
        logger.info { "Initializing MongoDB repository with config: $config, collection: $collectionName" }

        val connString = getConnectionString(config)

        client = MongoClient.create(connString)
        collection = client
            .getDatabase(config.database)
            .getCollection<MongoNode>(collectionName)

        logger.info { "MongoDB client initialized successfully" }
    }

    override suspend fun createNode(node: Node): IDbNodeResponse {
        try {
            val dbEntity = MongoNode.fromCommon(node)
            dbEntity.mongoId = ObjectId()
            dbEntity.id = UUID.randomUUID().toString()
            dbEntity.lock = UUID.randomUUID().toString()
            collection.insertOne(dbEntity)
            return DbNodeResponseOk(dbEntity.toCommon())
        } catch (e: Exception) {
            return DbNodeResponseErr(CommonError(message = e.message?:""))
        }
    }

    override suspend fun readNode(id: String): IDbNodeResponse {
        try {
            val dbEntity = collection
                .find(Filters.eq("id", id)).firstOrNull()

            return if (dbEntity != null) {
                DbNodeResponseOk(dbEntity.toCommon())
            } else {
                DbNodeResponseErr(CommonError(message = "not found"))
            }
        } catch (e: Exception) {
            return DbNodeResponseErr(CommonError(message = e.message?:""))
        }
    }

    override suspend fun updateNode(node: Node): IDbNodeResponse {
        try {
            val dbEntity = MongoNode.fromCommon(node)

            val existingEntity = collection
                .find(Filters.eq("id", dbEntity.id)).firstOrNull()

            if (existingEntity == null) {
                return DbNodeResponseErr(CommonError(message = "not found"))
            }

            if (existingEntity.lock != node.lock) {
                return DbNodeResponseErr(CommonError(message = "invalid lock"))
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
                DbNodeResponseOk(dbEntity.toCommon())
            } else {
                DbNodeResponseErr(CommonError(message = "node not found or lock mismatch"))
            }
        } catch (e: Exception) {
            return DbNodeResponseErr(CommonError(message = e.message?:""))
        }
    }

    override suspend fun deleteNode(id: String, lock: String): IDbNodeResponse {
        try {
            val existingEntity = collection
                .find(Filters.eq("id", id)).firstOrNull()

            if (existingEntity == null) {
                return DbNodeResponseErr(CommonError(message = "not found"))
            }

            if (existingEntity.lock != lock) {
                return DbNodeResponseErr(CommonError(message = "invalid lock"))
            }

            collection.deleteOne(
                Filters.and(
                    Filters.eq("_id", existingEntity?.mongoId),
                )
            )

            return DbNodeResponseOk(existingEntity?.toCommon() ?: Node())
        } catch (e: Exception) {
            return DbNodeResponseErr(CommonError(message = e.message?:""))
        }
    }

    override suspend fun searchNode(filter: NodeFilter): IDbNodesResponse {
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

            return DbNodesResponseOk(nodes)
        } catch (e: Exception) {
            return DbNodesResponseErr(CommonError(message = e.message?:""))
        }
    }

    suspend fun close() {
        client.close()
    }
}