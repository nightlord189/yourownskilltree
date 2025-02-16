package org.aburavov.yourownskilltree.backend.biz.repo

import org.aburavov.yourownskilltree.backend.common.model.*
import org.aburavov.yourownskilltree.backend.cor.Worker
import repo.*

class RepoCreate (private val repo: IRepoNode): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.command == NodeCommand.CREATE
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        ctx.nodeRequest?.ownerId = ctx.userId ?: ""
        val result = repo.createNode(ctx.nodeRequest?: Node())

        if (result.errors.isEmpty()) {
            ctx.nodeResponse = result.data
            return true
        } else {
            ctx.errors.addAll(result.errors)
            return false
        }
    }
}

class RepoRead (private val repo: IRepoNode): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.command == NodeCommand.READ
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        val result = repo.readNode(ctx.nodeIdRequest?:"")

        if (result.errors.isEmpty()) {
            ctx.nodeResponse = result.data
            return true
        } else {
            ctx.errors.addAll(result.errors)
            return false
        }
    }
}

class RepoUpdate (private val repo: IRepoNode): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.command == NodeCommand.UPDATE
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        val result = repo.updateNode(ctx.nodeRequest?:Node())

        if (result.errors.isEmpty()) {
            ctx.nodeResponse = result.data
            return true
        } else {
            ctx.errors.addAll(result.errors)
            return false
        }
    }
}

class RepoDelete (private val repo: IRepoNode): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.command == NodeCommand.DELETE
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        val result = repo.deleteNode(ctx.nodeIdRequest?:"", ctx.nodeLock?:"")

        if (result.errors.isEmpty()) {
            ctx.nodeResponse = result.data
            return true
        } else {
            ctx.errors.addAll(result.errors)
            return false
        }
    }
}

class RepoSearch (private val repo: IRepoNode): Worker<NodeContext>() {
    override suspend fun on(ctx: NodeContext): Boolean {
        return ctx.command == NodeCommand.SEARCH
    }

    override suspend fun handle(ctx: NodeContext): Boolean {
        val result = repo.searchNode(ctx.nodeFilterRequest?:NodeFilter())

        if (result.errors.isEmpty()) {
            ctx.nodesResponse = result.data?.toMutableList()
            return true
        } else {
            ctx.errors.addAll(result.errors)
            return false
        }
    }
}