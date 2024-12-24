package org.aburavov.yourownskilltree.backend.biz.stubs

import org.aburavov.yourownskilltree.backend.common.model.CommonError
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.NodeStubs
import org.aburavov.yourownskilltree.backend.cor.Worker

class UnsupportedStub ( private val stubCase: NodeStubs): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return context.stubCase == stubCase
    }

    override fun handle(context: NodeContext): Boolean {
        context.addError("unsupported stub")
        return false
    }
}

class StubDbError (): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return context.stubCase == NodeStubs.DB_ERROR
    }

    override fun handle(context: NodeContext): Boolean {
        context.addError("db error")
        return false
    }
}

class StubNotFoundError (): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return context.stubCase == NodeStubs.NOT_FOUND
    }

    override fun handle(context: NodeContext): Boolean {
        context.addError("not found")
        return false
    }
}

class StubCannotDeleteError (): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return context.stubCase == NodeStubs.CANNOT_DELETE
    }

    override fun handle(context: NodeContext): Boolean {
        context.addError("cannot delete the item")
        return false
    }
}

class StubBadIdError (): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return context.stubCase == NodeStubs.BAD_ID
    }

    override fun handle(context: NodeContext): Boolean {
        context.addError("bad id")
        return false
    }
}