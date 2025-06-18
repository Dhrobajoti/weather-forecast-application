# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Run stage (minimal runtime image)
FROM gcr.io/distroless/java17-debian11
WORKDIR /app

# Copy the jar only
COPY --from=builder /app/target/*.jar app.jar

# Set environment variables
ENV spring.cache.type=caffeine \
    spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=1h

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-Xmx256m","-jar", "app.jar"]
