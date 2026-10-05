# Phase 12 deployment example: build an executable Spring Boot JAR in one
# image, then run only the smaller JRE image in the final container.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /workspace/target/java-spring-learning-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
