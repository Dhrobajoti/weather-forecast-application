# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Build the application
COPY src ./src
RUN mvn package -DskipTests

# Run stage (minimal runtime image)
FROM gcr.io/distroless/java17-debian11
WORKDIR /app

# Copy the compiled JAR
COPY --from=builder /app/target/*.jar app.jar

# Declare volume for CSV data
# VOLUME ["/data/csv"]

# Expose service port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-Xmx256m","-jar", "app.jar"]