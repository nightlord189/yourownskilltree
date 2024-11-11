package org.aburavov.yourownskilltree.backend.common.model

class Debug {
    var stub: NodeStubs = NodeStubs.SUCCESS
    var workMode: WorkMode = WorkMode.STUB
}

enum class NodeStubs {
    NONE,
    SUCCESS,
    NOT_FOUND,
    BAD_ID,
    CANNOT_DELETE,
    BAD_SEARCH_STRING,
    DB_ERROR,
}

enum class WorkMode {
    PROD,
    TEST,
    STUB,
}
