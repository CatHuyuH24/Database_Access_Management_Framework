# Multi-stage build for DAM Framework Demo

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy framework and demo code
COPY dam-framework ./dam-framework
COPY dam-demo ./dam-demo

# Build framework first
WORKDIR /app/dam-framework
RUN mvn clean install -DskipTests

# Build demo application
WORKDIR /app/dam-demo
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy compiled JAR and dependencies
COPY --from=builder /app/dam-demo/target/*.jar app.jar
COPY --from=builder /root/.m2/repository /root/.m2/repository

# Install MySQL client for database inspection
RUN apk add --no-cache mysql-client

# Create startup script
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "Waiting for MySQL to be ready..."' >> /app/start.sh && \
    echo 'sleep 5' >> /app/start.sh && \
    echo 'java -jar /app/app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
