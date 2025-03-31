package org.aburavov.yourownskilltree.backend.spring.config

import AccessEntityRepoInMemory
import NodeRepoInMemory
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.WorkMode
import org.aburavov.yourownskilltree.backend.repo.mongo.AccessEntityRepoMongo
import org.aburavov.yourownskilltree.backend.repo.mongo.NodeRepoMongo
import org.aburavov.yourownskilltree.backend.stubs.AccessEntityRepoStub
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NodeConfig (private val mongoConfig: MongoConfig) {
    @Bean
    fun nodeProcessor(): NodeProcessor {
        return NodeProcessor(
            nodeRepos = mapOf(
                WorkMode.TEST to NodeRepoInMemory(),
                WorkMode.STUB to NodeRepoStub(),
                WorkMode.PROD to NodeRepoMongo(mongoConfig.toRepoConfig())
            ),
            accessEntityRepos = mapOf(
                WorkMode.TEST to AccessEntityRepoInMemory(),
                WorkMode.STUB to AccessEntityRepoStub(),
                WorkMode.PROD to AccessEntityRepoMongo(mongoConfig.toRepoConfig())
            )
        )
    }
}