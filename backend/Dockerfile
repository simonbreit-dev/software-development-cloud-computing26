# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jdk-jammy AS build

WORKDIR /workspace/backend

COPY backend/.mvn ./.mvn
COPY backend/mvnw backend/pom.xml ./

RUN chmod +x ./mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY openApiSpec.yaml /workspace/openApiSpec.yaml
COPY backend/api ./api
COPY backend/src ./src

ARG MAVEN_BUILD_ARGS="-DskipTests"
RUN ./mvnw -B ${MAVEN_BUILD_ARGS} package && \
    cp target/*.jar /tmp/sdfcc-backend.jar

FROM eclipse-temurin:25-jre-jammy AS runtime

RUN groupadd --system spring && \
    useradd --system --gid spring --home-dir /app --shell /usr/sbin/nologin spring

WORKDIR /app

ENV JAVA_OPTS="" \
    SERVER_PORT=8080

COPY --from=build /tmp/sdfcc-backend.jar /app/app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
