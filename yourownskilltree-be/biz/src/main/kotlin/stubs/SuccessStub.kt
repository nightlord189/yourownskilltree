package org.aburavov.yourownskilltree.backend.biz.stubs

import org.aburavov.yourownskilltree.backend.common.model.Node
import org.aburavov.yourownskilltree.backend.common.model.NodeContext
import org.aburavov.yourownskilltree.backend.common.model.NodeStatus
import org.aburavov.yourownskilltree.backend.common.model.NodeStubs
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
            id = ctx.nodeIdRequest!!
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
                name="node-search"
                description="node-search-description"
                status=NodeStatus.OPEN
                progress=0
                lock=UUID.randomUUID().toString()
            }
        )
        if (ctx.nodeFilterRequest?.nameLike != null) {
            ctx.nodesResponse!![0].name = "node-search-${ctx.nodeFilterRequest?.nameLike}"
        }
        if (ctx.nodeFilterRequest?.parentId != null) {
            ctx.nodesResponse!![0].parentIds = mutableListOf(ctx.nodeFilterRequest?.parentId!!)
        }
        return true
    }
}

class StubSuccessDelete (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = ctx.nodeIdRequest!!
            name="node-delete"
            description="node-delete-description"
            status=NodeStatus.OPEN
            progress=0
            lock=ctx.nodeLock!!
        }
        return true
    }
}


class StubSuccessCreate (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = UUID.randomUUID().toString()
            name = ctx.nodeRequest!!.name
            description = ctx.nodeRequest!!.description
            completionType = ctx.nodeRequest!!.completionType
            status = ctx.nodeRequest!!.status
            parentIds = ctx.nodeRequest!!.parentIds
            progress = ctx.nodeRequest!!.progress
            questions = ctx.nodeRequest!!.questions
            lock = UUID.randomUUID().toString()
        }
        return true
    }
}

class StubSuccessUpdate (): StubSuccess () {
    override fun handle(ctx: NodeContext): Boolean {
        ctx.nodeResponse = Node().apply {
            id = ctx.nodeRequest!!.id
            name = ctx.nodeRequest!!.name
            description = ctx.nodeRequest!!.description
            completionType = ctx.nodeRequest!!.completionType
            status = ctx.nodeRequest!!.status
            parentIds = ctx.nodeRequest!!.parentIds
            progress = ctx.nodeRequest!!.progress
            questions = ctx.nodeRequest!!.questions
            lock = UUID.randomUUID().toString()
        }
        return true
    }
}
