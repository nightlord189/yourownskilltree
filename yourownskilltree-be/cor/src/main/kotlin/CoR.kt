package org.aburavov.yourownskilltree.backend.cor

abstract class Worker<T> {
    /**
     * возвращает True, если может обработать объект
     */
    abstract fun on (ctx: T): Boolean

    /**
    * возвращает True, если цепочка может быть продолжена или False, если она должна быть остановлена на этом обработчике
    */
    abstract fun handle (ctx: T): Boolean
}

/**
 * Цепочка воркеров
 */
class Chain<T>(vararg workers: Worker<T>) {
    private val workers: List<Worker<T>> = workers.toList()

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