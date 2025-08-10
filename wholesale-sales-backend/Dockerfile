# ===== Stage 1: Build the application =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build application without running tests
RUN mvn clean package -DskipTests

# ===== Stage 2: Run the application =====
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for Render
ENV PORT=8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
