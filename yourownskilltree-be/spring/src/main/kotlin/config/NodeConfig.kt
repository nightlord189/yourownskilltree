package org.aburavov.yourownskilltree.backend.spring.config

import org.aburavov.yourownskilltree.backend.biz.NodeProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NodeConfig {
    @Bean
    fun nodeProcessor(): NodeProcessor {
        return NodeProcessor()
    }
}