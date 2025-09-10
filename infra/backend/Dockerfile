# ===== Build Stage =====
FROM gradle:8.9-jdk17-alpine AS builder
WORKDIR /workspace

# 依存関係キャッシュ用
COPY backend/build.gradle backend/settings.gradle backend/gradle.properties* ./backend/
COPY backend/gradle ./backend/gradle
WORKDIR /workspace/backend
RUN gradle --no-daemon dependencies || true

# アプリ本体
COPY backend/ /workspace/backend
RUN gradle --no-daemon clean bootJar

# ===== Runtime Stage =====
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=builder /workspace/backend/build/libs/*.jar app.jar

EXPOSE 8080
ENV JAVA_TOOL_OPTIONS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8
ENTRYPOINT ["java","-jar","/app/app.jar"]
