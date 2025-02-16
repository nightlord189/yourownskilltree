package org.aburavov.yourownskilltree.backend.common.permissions

enum class Permission {
    READ,
    READ_FULL,
    CREATE,
    UPDATE,
    DELETE
} // пермишены по отношению к конкретной ноде

enum class UserGroup {
    ADMIN,        // администратор (поддержка)
    USER,         // обычный пользователь
    GUEST,        // гость
}

enum class NodeAccessLevel {
    FULL_ACCESS,          // полный доступ (чтение всего + редактирование)
    FULL_READ,           // полный доступ на чтение (без редактирования)
    GENERAL_READ,        // доступ только к общей информации (без вопросов)
}