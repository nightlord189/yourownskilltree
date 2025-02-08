package permissions

enum class NodePrincipalRelations {
    NONE,         // нет отношения к ноде
    OWN,          // создатель ноды
    SHARED,       // нода расшарена этому пользователю
    PUBLIC,       // публичный доступ
}

enum class UserGroups {
    USER,         // обычный пользователь
    GUEST,        // гость
}

enum class NodeAccessLevel {
    FULL_ACCESS,          // полный доступ (чтение всего + редактирование)
    FULL_READ,           // полный доступ на чтение (без редактирования)
    GENERAL_READ,        // доступ только к общей информации
    PROGRESS_ONLY,       // доступ только к прохождению тестов и заполнению прогресса
}

// по сути достаточно знать, какой у текущего юзера к текущей ноде NodeAccessLevel