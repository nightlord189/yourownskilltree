package org.aburavov.yourownskilltree.backend.repo.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import mu.KotlinLogging
import kotlin.reflect.KClass

open class MongoRepo<T: Any> (
    config: MongoConfig,
    collectionName: String = "",
    private val documentClass: KClass<T>
) {
    protected val logger = KotlinLogging.logger {}

    protected lateinit var client: MongoClient
    protected lateinit var collection: MongoCollection<T>

    init {
        println("DEBUG: Initializing MongoDB repository")
        logger.info { "Initializing MongoDB repository with config: $config, collection: $collectionName" }

        val connString = getConnectionString(config)

        client = MongoClient.create(connString)
        collection = client
            .getDatabase(config.database)
            .getCollection(collectionName, documentClass.java)

        logger.info { "MongoDB client initialized successfully" }
    }

    suspend fun close() {
        client.close()
    }
}