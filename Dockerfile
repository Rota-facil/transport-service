FROM eclipse-temurin:jre-21-jammy

WORKDIR /app

COPY target/*.app app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]