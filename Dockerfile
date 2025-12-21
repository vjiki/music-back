# Use JDK 21 image (matching build.gradle.kts Kotlin JVM toolchain)
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle wrapper and config files
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Pre-fetch dependencies (optional but speeds up build)
# This layer will be cached if dependencies don't change
RUN ./gradlew dependencies --no-daemon || true

# Copy remaining source files (Kotlin sources and resources)
COPY . .

# Build the JAR (Kotlin will be compiled to JVM bytecode)
RUN ./gradlew clean build -x test --no-daemon

# =========================
# Run stage
# =========================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built JAR from previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the app (Kotlin compiled to JVM bytecode runs the same way)
ENTRYPOINT ["java", "-jar", "app.jar"]
