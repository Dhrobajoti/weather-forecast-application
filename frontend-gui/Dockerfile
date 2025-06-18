# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Run stage using a minimal, secure image
FROM gcr.io/distroless/java17-debian11
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Environment configuration
ENV backend.api.url=http://backend-api:8081 \
    backend.csv.url=http://backend-csv:8082 \
    backend.graph.url=http://backend-graph:8083 \
    backend.db.url=http://backend-db:8084 \
    backend.s3.url=http://backend-s3:8085 \
    backend.dynamodb.url=http://backend-dynamodb:8086

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]