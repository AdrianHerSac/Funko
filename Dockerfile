# Etapa 1: Build
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY gradlew ./
COPY gradlew.bat ./
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

COPY src ./src

# Ejecutamos Gradle para generar el JAR ejecutable
RUN ./gradlew clean bootJar -x test --no-daemon

# Etapa 2: Run
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Exponemos el puerto de la API
EXPOSE 6969

# Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]