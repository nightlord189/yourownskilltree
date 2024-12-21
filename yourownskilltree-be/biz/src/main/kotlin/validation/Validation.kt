package org.aburavov.yourownskilltree.backend.biz.validation

import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.cor.Worker

fun validateReadRequest (ctx: NodeContext): Boolean {
    if (ctx.nodeRequest?.id?.isEmpty() == true) {
        ctx.addError("id must be filled for read request")
    }
    return true
}

class Validator(
    private val handleFn: (NodeContext) -> Boolean
) : Worker<NodeContext>() {
    override fun on(context: NodeContext) = true

    override fun handle(context: NodeContext): Boolean {
        return handleFn(context)
    }
}

class FinishValidator(): Worker<NodeContext>() {
    override fun on(context: NodeContext): Boolean {
        return true
    }

    override fun handle(context: NodeContext): Boolean {
        return context.errors.isEmpty()
    }
}
