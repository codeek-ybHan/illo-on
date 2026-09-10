# ── build ──
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# 저메모리(1GB) 서버에서 빌드가 OOM-kill 되지 않도록 Maven 힙 상한 지정
ENV MAVEN_OPTS="-Xmx512m"
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ── run ──
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/illo-on-backend-*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
