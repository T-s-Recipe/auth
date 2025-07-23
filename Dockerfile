FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

RUN gradle dependencies --build-cache || true

COPY src ./src

RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY src/main/resources ./resources
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8010

ARG ANDROID_CLIENT_ID
ENV ANDROID_CLIENT_ID=${ANDROID_CLIENT_ID}

ARG IOS_CLIENT_ID
ENV IOS_CLIENT_ID=${IOS_CLIENT_ID}

ARG PROFILE
ENV PROFILE=${PROFILE}

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=$PROFILE -jar app.jar"]