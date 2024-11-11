package org.aburavov.yourownskilltree.backend.common.model

import java.time.LocalDateTime

open class RequestContext {
    var errors: MutableList<CommonError> = mutableListOf()
    var requestId: String? = null
    var timeStart: LocalDateTime? = null

    var workMode: WorkMode = WorkMode.PROD
    var stubCase: NodeStubs = NodeStubs.NONE
}
