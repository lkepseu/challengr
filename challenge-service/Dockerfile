FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 🧩 Script d’entrée
RUN echo '#!/bin/sh' > /entrypoint.sh && \
    echo 'echo "🚀 Starting app with JAVA_TOOL_OPTIONS=$JAVA_TOOL_OPTIONS"' >> /entrypoint.sh && \
    echo 'exec java $JAVA_TOOL_OPTIONS -jar /app/app.jar' >> /entrypoint.sh && \
    chmod +x /entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
