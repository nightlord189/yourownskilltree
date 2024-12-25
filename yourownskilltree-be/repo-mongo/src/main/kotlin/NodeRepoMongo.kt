import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeFilter
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import repo.*

class NodeRepoMongo (
    connectionString: String,
    databaseName: String="master",
    collectionName: String = "nodes"
): IRepoNode {
    private val client = MongoClient.create(connectionString)
    private val collection = client
        .getDatabase(databaseName)
        .getCollection<MongoNode>(collectionName)

    override suspend fun createNode(node: Node): IDbNodeResponse {
        try {
            val dbEntity = MongoNode.fromCommon(node)
            dbEntity.mongoId = ObjectId()
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

            if (existingEntity?.lock != node.lock) {
                return DbNodeResponseErr(CommonError(message = "invalid lock"))
            }

            dbEntity.mongoId = existingEntity?.mongoId?: ObjectId()

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