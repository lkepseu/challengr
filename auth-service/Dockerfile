# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Téléchargement de l’agent OpenTelemetry (dernière version stable)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

EXPOSE 8082

# Variables recommandées
ENV OTEL_SERVICE_NAME=auth-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector.logging.svc.cluster.local:4317
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar /app/app.jar"]
