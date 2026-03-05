FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY gateway/build/libs/*.jar app.jar
EXPOSE 11080
ENTRYPOINT ["java", "-jar", "app.jar"]