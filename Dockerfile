FROM gradle:jdk21-jammy AS build

WORKDIR /app

COPY . .

RUN ./gradlew :yourownskilltree-be:internal:build --no-daemon --info

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/yourownskilltree-be/internal/build/libs/*.jar ./yourownskilltree-be.jar

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "yourownskilltree-be.jar"]