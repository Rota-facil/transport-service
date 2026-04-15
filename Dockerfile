FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/*.app app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]