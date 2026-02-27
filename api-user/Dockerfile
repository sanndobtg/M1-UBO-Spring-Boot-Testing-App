FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 11081
ENTRYPOINT ["java", "-jar", "app.jar"]