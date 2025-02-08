package org.aburavov.yourownskilltree.backend.biz

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.biz.auth.CheckIsAuthorized
import org.aburavov.yourownskilltree.backend.biz.auth.CheckPermissions
import org.aburavov.yourownskilltree.backend.stubs.*
import org.aburavov.yourownskilltree.backend.biz.repo.*
import org.aburavov.yourownskilltree.backend.biz.validation.*
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.cor.Chain
import repo.IRepoNode

class NodeProcessor (
    private val repos: Map<WorkMode, IRepoNode> = mapOf(
        WorkMode.STUB to NodeRepoStub(),
    ),
) {
    private val logger = KotlinLogging.logger {}

    suspend fun process(ctx: NodeContext) {
        logger.info { "processing NodeContext with workMode ${ctx.workMode}, stub ${ctx.stubCase} and command ${ctx.command}" }

        val repo = repos[ctx.workMode]
        if (repo == null) {
            ctx.addError("repo is null")
            return
        }

        when (ctx.command) {
            NodeCommand.CREATE -> {
                Chain<NodeContext>(
                    CheckPermissions(repo),
                    Validator(::validateRequest),
                    Validator(::validateName),
                    Validator(::validateBusiness),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.NOT_FOUND),
                    UnsupportedStub(NodeStubs.BAD_ID),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubDbError(),
                    RepoCreate(repo),
                ).run(ctx)
            }
            NodeCommand.READ -> {
                Chain<NodeContext>(
                    Validator(::validateIdRequest),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubDbError(),
                    RepoRead(repo),
                    CheckPermissions(repo),
                ).run(ctx)
            }
            NodeCommand.UPDATE -> {
                Chain<NodeContext>(
                    CheckIsAuthorized(),
                    Validator(::validateRequest),
                    Validator(::validateId),
                    Validator(::validateName),
                    Validator(::validateBusiness),
                    Validator(::validateLock),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubDbError(),
                    RepoRead(repo),
                    CheckPermissions(repo),
                    RepoUpdate(repo),
                ).run(ctx)
            }
            NodeCommand.DELETE -> {
                Chain<NodeContext>(
                    CheckIsAuthorized(),
                    Validator(::validateIdRequest),
                    Validator(::validateLockRequest),
                    ValidatorFinish(),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubCannotDeleteError(),
                    StubDbError(),
                    RepoRead(repo),
                    CheckPermissions(repo),
                    RepoDelete(repo),
                ).run(ctx)
            }
            NodeCommand.SEARCH -> {
                Chain<NodeContext>(
                    Validator(::validateFilter),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.BAD_ID),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubDbError(),
                    RepoSearch(repo),
                    CheckPermissions(repo), // потому что в случае поиска у юзера может не быть прав на какие-то из найденных нод
                ).run(ctx)
            }
            NodeCommand.NONE -> throw Exception("unknown command")
        }
    }
}