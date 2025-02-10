package permissions

enum class UserGroups {
    ADMIN,        // администратор (поддержка)
    USER,         // обычный пользователь
    GUEST,        // гость
}

enum class NodeAccessLevel {
    FULL_ACCESS,          // полный доступ (чтение всего + редактирование)
    FULL_READ,           // полный доступ на чтение (без редактирования)
    GENERAL_READ,        // доступ только к общей информации (без вопросов)
}