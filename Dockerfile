# Use a lightweight JDK 25 image
FROM eclipse-temurin:25-jdk-jammy

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies (for caching)
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the source
COPY . .

# Build the app
RUN ./gradlew clean build -x test --no-daemon

# Expose the port your app runs on (change if needed)
EXPOSE 8080

# Run the Spring Boot JAR
CMD ["java", "-jar", "build/libs/app.jar"]
