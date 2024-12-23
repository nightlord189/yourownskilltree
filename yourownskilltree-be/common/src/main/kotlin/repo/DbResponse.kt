package repo

import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.Node

sealed interface IDbResponse<T>

sealed interface IDbNodeResponse: IDbResponse<Node>
sealed interface IDbNodesResponse: IDbResponse<List<Node>>

data class DbNodeResponseOk(
    val data: Node
): IDbNodeResponse

data class DbNodeResponseErr(
    val errors: List<CommonError> = emptyList()
): IDbNodeResponse {
    constructor(err: CommonError): this(listOf(err))
}

data class DbNodesResponseOk(
    val data: List<Node>
): IDbNodesResponse

data class DbNodesResponseErr(
    val errors: List<CommonError> = emptyList()
): IDbNodesResponse {
    constructor(err: CommonError): this(listOf(err))
}

