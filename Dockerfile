# --- Stage 1: Build Stage ---
# Use an official Maven image to build the application.
# The 'eclipse-temurin' images are recommended by Spring Boot.
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# --- Stage 2: Runtime Stage ---
# Use a lightweight JRE image for the final container.
# This results in a smaller and more secure image.
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]