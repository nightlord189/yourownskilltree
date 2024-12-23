package org.aburavov.yourownskilltree.backend.biz

import mu.KotlinLogging
import org.aburavov.yourownskilltree.backend.biz.stubs.*
import org.aburavov.yourownskilltree.backend.biz.validation.*
import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.cor.Chain
import org.aburavov.yourownskilltree.backend.cor.Worker
import java.util.*

class NodeProcessor {
    private val logger = KotlinLogging.logger {}

    fun process(ctx: NodeContext) {
        logger.info { "processing NodeContext with workMode ${ctx.workMode} and command ${ctx.command}" }

        when (ctx.workMode) {
            WorkMode.PROD, WorkMode.TEST -> {
                ctx.errors.add(CommonError(message="unsupported workMode"))
                return
            }
            WorkMode.STUB -> {}
        }

        when (ctx.command) {
            NodeCommand.CREATE -> {
                Chain<NodeContext>(
                    Validator(::validateRequest),
                    Validator(::validateName),
                    Validator(::validateBusiness),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.NONE),
                    UnsupportedStub(NodeStubs.NOT_FOUND),
                    UnsupportedStub(NodeStubs.BAD_ID),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubDbError(),
                    StubSuccessCreate(),
                ).run(ctx)
            }
            NodeCommand.READ -> {
                Chain<NodeContext>(
                    Validator(::validateIdRequest),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.NONE),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubDbError(),
                    StubSuccessRead()
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
                    UnsupportedStub(NodeStubs.NONE),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubDbError(),
                    StubSuccessUpdate()
                ).run(ctx)
            }
            NodeCommand.DELETE -> {
                Chain<NodeContext>(
                    Validator(::validateIdRequest),
                    Validator(::validateLockRequest),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.NONE),
                    StubNotFoundError(),
                    StubBadIdError(),
                    StubCannotDeleteError(),
                    StubDbError(),
                    StubSuccessDelete(),
                ).run(ctx)
            }
            NodeCommand.SEARCH -> {
                Chain<NodeContext>(
                    Validator(::validateFilter),
                    ValidatorFinish(),
                    UnsupportedStub(NodeStubs.NONE),
                    UnsupportedStub(NodeStubs.BAD_ID),
                    UnsupportedStub(NodeStubs.CANNOT_DELETE),
                    StubNotFoundError(),
                    StubDbError(),
                    StubSuccessSearch()
                ).run(ctx)
            }
            NodeCommand.NONE -> throw Exception("unknown command")
        }
    }
}