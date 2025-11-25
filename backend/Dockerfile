# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Nur pom.xml kopieren für besseres Layer-Caching
COPY pom.xml .
# Abhängigkeiten laden (Go-Offline)
RUN mvn dependency:go-offline -B
# Source Code kopieren und bauen
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Nur das JAR aus der Build-Stage kopieren
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
