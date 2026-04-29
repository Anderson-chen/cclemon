# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copy build scripts first — lets Docker cache the dependency-download layer
COPY build.gradle settings.gradle ./
COPY cclemon-auth/build.gradle cclemon-auth/
COPY cclemon-logging/build.gradle cclemon-logging/
COPY cclemon-utils/build.gradle cclemon-utils/

# Gradle requires every directory listed in settings.gradle to exist,
# even for projects we don't compile — create stubs for the rest.
RUN mkdir -p cclemon-health cclemon-security cclemon-practice cclemon-stream \
    cclemon-config-server cclemon-data cclemon-interface cclemon-cache \
    cclemon-kafka cclemon-web cclemon-consumer cclemon-api cclemon-job

RUN ./gradlew :cclemon-auth:dependencies --no-daemon -q

# Copy source after deps to maximise cache hits on source-only changes
COPY cclemon-auth/src/ cclemon-auth/src/
COPY cclemon-logging/src/ cclemon-logging/src/
COPY cclemon-utils/src/ cclemon-utils/src/

RUN ./gradlew :cclemon-auth:bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/cclemon-auth/build/libs/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]