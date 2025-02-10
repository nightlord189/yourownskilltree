package repo

import model.AccessEntity
import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node


interface DbResponse<T> {
    val data: T?
    val errors: List<CommonError>
}

data class DbNodeResponse(
    override val data: Node? = null,
    override val errors: List<CommonError> = emptyList()
) : DbResponse<Node>

data class DbNodesResponse(
    override val data: List<Node>? = null,
    override val errors: List<CommonError> = emptyList()
) : DbResponse<List<Node>>

data class DbAccessEntityResponse(
    override val data: AccessEntity? = null,
    override val errors: List<CommonError> = emptyList()
) : DbResponse<AccessEntity>
