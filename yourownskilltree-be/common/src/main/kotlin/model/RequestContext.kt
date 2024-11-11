package org.aburavov.yourownskilltree.backend.common.model

import java.time.LocalDateTime

open class RequestContext {
    val errors: MutableList<Error> = mutableListOf()
    val requestID: String? = null
    var timeStart: LocalDateTime? = null

    var workMode: WorkMode = WorkMode.PROD
    var stubCase: NodeStubs = NodeStubs.NONE
}
