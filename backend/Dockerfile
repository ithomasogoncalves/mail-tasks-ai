# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# ... (Instruções de build)
RUN ./mvnw package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# ... (Instruções de execução)
ENTRYPOINT ["java", "-jar", "app.jar"]
