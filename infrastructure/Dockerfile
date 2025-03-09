FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ../target/epam-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]