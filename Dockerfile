# Multi-stage build: compile with Maven Wrapper, run fat jar with JRE

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache bash

COPY . .
RUN chmod +x mvnw \
    && ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/notification-examples/target/notification-examples-1.0.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
