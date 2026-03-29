FROM eclipse-temurin:17-jdk-alpine AS build
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
RUN apk add --no-cache maven && mvn -f /app/pom.xml dependency:go-offline -pl config-server -am -q
COPY config-server/src /app/config-server/src
COPY config-repo /app/config-repo
RUN mvn -f /app/pom.xml clean package -pl config-server -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
COPY --from=build /app/config-repo /config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
