FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY eureka-server/pom.xml /app/eureka-server/pom.xml
COPY config-server/pom.xml /app/config-server/pom.xml
COPY api-gateway/pom.xml /app/api-gateway/pom.xml
COPY user-service/pom.xml /app/user-service/pom.xml
COPY salon-service/pom.xml /app/salon-service/pom.xml
COPY appointment-service/pom.xml /app/appointment-service/pom.xml
COPY notification-service/pom.xml /app/notification-service/pom.xml
COPY system-events-service/pom.xml /app/system-events-service/pom.xml
RUN mvn -f /app/pom.xml dependency:go-offline -pl system-events-service -am -q
COPY system-events-service/src /app/system-events-service/src
RUN mvn -f /app/pom.xml clean package -pl system-events-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/system-events-service/target/*.jar app.jar
EXPOSE 8085
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
