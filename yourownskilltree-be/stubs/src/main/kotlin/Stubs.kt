package org.aburavov.yourownskilltree.backend.stubs

import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.NodeStubs
import org.aburavov.yourownskilltree.backend.cor.Worker

class UnsupportedStub ( private val stubCase: NodeStubs): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == stubCase
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.addError("unsupported stub")
        return false
    }
}

class StubDbError (): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == NodeStubs.DB_ERROR
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.addError("db error")
        return false
    }
}

class StubNotFoundError (): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == NodeStubs.NOT_FOUND
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.addError("not found")
        return false
    }
}

class StubCannotDeleteError (): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == NodeStubs.CANNOT_DELETE
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.addError("cannot delete the item")
        return false
    }
}

class StubBadIdError (): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == NodeStubs.BAD_ID
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.addError("bad id")
        return false
    }
}