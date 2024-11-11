package org.aburavov.yourownskilltree.backend.common.model

data class Error(
    val code: String = "",
    val group: String = "",
    val field: String = "",
    val message: String = "",
    val exception: Throwable? = null,
)
