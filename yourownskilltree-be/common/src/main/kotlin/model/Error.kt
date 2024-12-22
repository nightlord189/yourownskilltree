package org.aburavov.yourownskilltree.backend.common.model

data class CommonError(
    var code: String = "",
    var group: String = "",
    var field: String = "",
    var message: String = "",
    var exception: Throwable? = null,
)
