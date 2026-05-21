# syntax=docker/dockerfile:1

# --- Build (official Gradle image — не зависит от gradle-wrapper.jar в git) ---
FROM gradle:8.14-jdk21-alpine AS build
WORKDIR /workspace

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle/
COPY app app/
COPY modules modules/

RUN gradle :app:bootJar -x test --no-daemon

# --- Runtime ---
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S casino && adduser -S casino -G casino \
    && mkdir -p /app/data/storage \
    && chown -R casino:casino /app/data

COPY --from=build /workspace/app/build/libs/*.jar /app/app.jar
RUN chown casino:casino /app/app.jar

USER casino

EXPOSE 8080

VOLUME ["/app/data/storage"]

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=prod \
    CASINO_STORAGE_LOCAL_DIR=/app/data/storage

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
