package org.aburavov.yourownskilltree.backend.biz

import mu.KotlinLogging
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
                ).run(ctx)
            }
            NodeCommand.UPDATE -> {
                Chain<NodeContext>(
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
                    RepoUpdate(repo),
                ).run(ctx)
            }
            NodeCommand.DELETE -> {
                Chain<NodeContext>(
                    Validator(::validateIdRequest),
                    Validator(::validateLockRequest),
                    ValidatorFinish(),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubCannotDeleteError(),
                    StubDbError(),
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
                ).run(ctx)
            }
            NodeCommand.NONE -> throw Exception("unknown command")
        }
    }
}