package org.aburavov.yourownskilltree.backend.biz.stubs

import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.cor.Worker
import java.util.*

abstract class StubSuccess (): Worker<NodeContext>(){
    override fun on(ctx: NodeContext): Boolean {
        return ctx.stubCase == NodeStubs.SUCCESS
    }
}

class StubSuccessRead (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = ctx.nodeIdRequest ?: UUID.randomUUID().toString()
            name="node-read"
            description="node-read-description"
            status=NodeStatus.OPEN
            progress=0
            lock=UUID.randomUUID().toString()
        }
        return true
    }
}

class StubSuccessSearch(): StubSuccess() {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodesResponse = mutableListOf(
            Node().apply {
                id = UUID.randomUUID().toString()
                name= if (ctx.nodeFilterRequest?.nameLike != null) "node-search-${ctx.nodeFilterRequest?.nameLike}" else "node-search"
                description="node-search-description"
                status=NodeStatus.OPEN
                progress=0
                parentIds = if (ctx.nodeFilterRequest?.parentId != null) listOf(ctx.nodeFilterRequest?.parentId ?: "") else emptyList()
                lock=UUID.randomUUID().toString()
            }
        )
        return true
    }
}

class StubSuccessDelete (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = ctx.nodeIdRequest ?: UUID.randomUUID().toString()
            name="node-delete"
            description="node-delete-description"
            status=NodeStatus.OPEN
            progress=0
            lock=ctx.nodeLock ?: ""
        }
        return true
    }
}


class StubSuccessCreate (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = UUID.randomUUID().toString()
            name = ctx.nodeRequest?.name ?: ""
            description = ctx.nodeRequest?.description
            completionType = ctx.nodeRequest?.completionType ?: NodeCompletionType.BOOL
            status = ctx.nodeRequest?.status ?: NodeStatus.OPEN
            parentIds = ctx.nodeRequest?.parentIds ?: emptyList()
            progress = ctx.nodeRequest?.progress
            questions = ctx.nodeRequest?.questions
            lock = UUID.randomUUID().toString()
        }
        return true
    }
}

class StubSuccessUpdate (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = ctx.nodeRequest?.id ?: UUID.randomUUID().toString()
            name = ctx.nodeRequest?.name ?: ""
            description = ctx.nodeRequest?.description
            completionType = ctx.nodeRequest?.completionType ?: NodeCompletionType.BOOL
            status = ctx.nodeRequest?.status ?: NodeStatus.OPEN
            parentIds = ctx.nodeRequest?.parentIds ?: emptyList()
            progress = ctx.nodeRequest?.progress
            questions = ctx.nodeRequest?.questions
            lock = UUID.randomUUID().toString()
        }
        return true
    }
}
