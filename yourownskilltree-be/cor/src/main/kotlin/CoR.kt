package org.aburavov.yourownskilltree.backend.cor

abstract class Worker<T> {
    abstract fun on (context: T): Boolean

    abstract fun handle (context: T): Boolean
}

class Chain<T>(private var workers: MutableList<Worker<T>>) {
    fun run (ctx: T) {
        var idx = 0

        while (idx < workers.count()) {
            val currentWorker = workers[idx]

            if (currentWorker.on(ctx)) {
                if (!currentWorker.handle(ctx)) {
                    break
                }
            }

            idx++
        }
    }
}