package org.aburavov.yourownskilltree.backend.spring.config

import MongoConfig
import NodeRepoInMemory
import NodeRepoMongo
import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.aburavov.yourownskilltree.backend.common.model.WorkMode
import org.aburavov.yourownskilltree.backend.stubs.NodeRepoStub
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import repo.IRepoNode

@Configuration
class NodeConfig {
    @Bean
    fun nodeProcessor(): NodeProcessor {
        return NodeProcessor(mapOf(
            WorkMode.TEST to NodeRepoInMemory(),
            WorkMode.STUB to NodeRepoStub(),
            WorkMode.PROD to NodeRepoMongo(MongoConfig())
        ))
    }
}