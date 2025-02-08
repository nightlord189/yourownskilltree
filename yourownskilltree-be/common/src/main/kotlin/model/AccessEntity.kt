package model

import permissions.NodeAccessLevel
import java.time.LocalDateTime

data class AccessEntity(
    val id: String,
    val resourceId: String,
    val userId: String,
    val accessLevel: NodeAccessLevel,
    val grantedAt: LocalDateTime
)