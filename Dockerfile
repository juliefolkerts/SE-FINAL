# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew --version

COPY src src
RUN ./gradlew bootJar -x test

# Runtime stage
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
