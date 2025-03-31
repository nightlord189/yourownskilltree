package model

import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup

data class Principal(
    val userId: String = "",
    val userGroup: UserGroup,
)