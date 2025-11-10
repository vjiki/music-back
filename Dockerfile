# Use lightweight JDK 25 image
FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle wrapper and config files
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Pre-fetch dependencies (optional but speeds up build)
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY . .

# Build the JAR
RUN ./gradlew clean build -x test --no-daemon

# =========================
# Run stage
# =========================
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copy built JAR from previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
