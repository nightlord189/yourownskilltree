package ru.otus.otuskotlin.m1l5.sql

class SqlSelectBuilder {
    private var table: String? = null
    private var cols = mutableListOf<String>()
    private var where: String? = null

    fun build(): String {
        if (table == null)
            throw IllegalStateException("table is null")

        val selectExpr = if (cols.size == 0) "*" else cols.joinToString(", " )

        val whereExpr = if (where == null) "" else " where $where"

        return "select $selectExpr from $table$whereExpr"
    }

    fun from(table: String) {
        this.table = table
    }

    fun select (vararg cols: String) {
        this.cols.addAll(cols)
    }

    fun where (expr: String) {
        this.where = expr
    }
}

fun query(block: SqlSelectBuilder.() -> Unit): SqlSelectBuilder {
    // Создаем новый экземпляр SqlSelectBuilder
    val builder = SqlSelectBuilder()

    // Вызываем переданную функцию block на этом экземпляре
    builder.block()

    // Возвращаем настроенный экземпляр SqlSelectBuilder
    return builder
}

infix fun String.eq(value2: String): String{
    return "$this = '$value2'"
}

infix fun String.eq(value2: Number): String{
    return "$this = $value2"
}

infix fun String.nonEq(value2: String?): String{
    if (value2 == null) return "$this !is null"
    return "$this != '$value2'"
}

infix fun String.nonEq(value2: Number): String{
    return "$this != $value2"
}

fun or (vararg expressions: String): String {
    return "(${expressions.joinToString(" or ")})"
}
