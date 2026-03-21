# Builder
FROM gradle:jdk21-alpine as builder
LABEL authors="luiz"
WORKDIR /usr/app/src
COPY . .
RUN gradle dependencies --no-daemon
RUN ./gradlew build -x test --no-daemon

# Jlink apenas bibliotecas necessarias
FROM eclipse-temurin:21-jdk-alpine AS jlink-builder
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.sql,java.naming,java.desktop,java.management,java.instrument,java.security.jgss,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /javaruntime

# Final
FROM alpine:latest
ENV JAVA_HOME=/opt/java-runtime
ENV PATH="$JAVA_HOME/bin:$PATH"

COPY --from=jlink-builder /javaruntime $JAVA_HOME

WORKDIR /usr/app/src
COPY --from=builder /usr/app/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]