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

# Sensible JVM defaults for containers.
# You can override any of these at deploy time by setting JAVA_TOOL_OPTIONS in your platform (Render).
#
# Notes:
# - JDK 21 default GC is G1 already; keeping it explicit is fine.
# - MaxRAMPercentage leaves headroom for non-heap (metaspace/native/threads).
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=80 -XX:InitialRAMPercentage=40 -XX:+ExitOnOutOfMemoryError"

# Run the app (Kotlin compiled to JVM bytecode runs the same way)
ENTRYPOINT ["java", "-jar", "app.jar"]
