package org.aburavov.yourownskilltree.backend.biz.validation

import org.aburavov.yourownskilltree.backend.common.model.NodeCompletionType
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.cor.Worker

fun validateIdRequest (ctx: NodeContext): Boolean {
    if (ctx.nodeIdRequest == null) {
        ctx.addError("id must be filled")
    }
    return true
}

fun validateLockRequest(ctx: NodeContext): Boolean {
    if (ctx.nodeLock == null) {
        ctx.addError("lock must be filled")
    }
    return true
}

fun validateLock(ctx: NodeContext): Boolean {
    if (ctx.nodeRequest?.lock == "") {
        ctx.addError("lock must be filled")
    }
    return true
}

fun validateId (ctx: NodeContext): Boolean {
    if (ctx.nodeRequest?.id == "") {
        ctx.addError("id must be filled")
    }
    return true
}

fun validateName (ctx: NodeContext): Boolean {
    if (ctx.nodeRequest?.name == "") {
        ctx.addError("name is empty")
    }
    return true
}

fun validateRequest (ctx: NodeContext): Boolean {
    if (ctx.nodeRequest == null) {
        ctx.addError("request is empty")
    }
    return true
}

fun validateBusiness (ctx: NodeContext): Boolean {
    if (ctx.nodeRequest?.completionType == NodeCompletionType.PERCENTAGE) {
        if ((ctx.nodeRequest?.progress ?: 0) < 0 || (ctx.nodeRequest?.progress ?: 0) > 100) {
            ctx.addError("progress must be equal to [0;100] with PERCENTAGE completion")
        }
    }
    if (ctx.nodeRequest?.completionType == NodeCompletionType.TEST && ctx.nodeRequest?.questions.isNullOrEmpty()) {
        ctx.addError("at least one question must be filled for TEST completion")
    }
    return true
}

fun validateFilter (ctx: NodeContext): Boolean {
    if (ctx.nodeFilterRequest == null) {
        ctx.addError("filter must be filled")
        return true
    }
    if (ctx.nodeFilterRequest?.parentId == null && ctx.nodeFilterRequest?.nameLike ==null) {
        ctx.addError("parent_id or name_like must be filled")
    }
    return true
}

/**
 * Хелпер для валидации, чтобы просто подсовывать функцию
 */
class Validator(
    private val handleFn: (NodeContext) -> Boolean
) : Worker<NodeContext>() {
    override fun on(ctx: NodeContext) = true

    override fun handle(ctx: NodeContext): Boolean {
        return handleFn(ctx)
    }
}

/**
 * Прерывает обработку, если есть хоть одна ошибка валидации
 */
class ValidatorFinish(): Worker<NodeContext>() {
    override fun on(ctx: NodeContext): Boolean {
        return true
    }

    override fun handle(ctx: NodeContext): Boolean {
        return ctx.errors.isEmpty()
    }
}
