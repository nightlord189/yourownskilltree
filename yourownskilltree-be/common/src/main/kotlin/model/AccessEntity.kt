package model

import org.aburavov.yourownskilltree.backend.common.permissions.NodeAccessLevel
import java.time.LocalDateTime

data class AccessEntity(
    val userId: String,
    val resourceId: String,
    val accessLevel: NodeAccessLevel,
    val grantedAt: LocalDateTime = LocalDateTime.now()
)