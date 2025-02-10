package model

import permissions.NodeAccessLevel
import java.time.LocalDateTime

data class AccessEntity(
    val userId: String,
    val resourceId: String,
    val accessLevel: NodeAccessLevel,
    val grantedAt: LocalDateTime
)