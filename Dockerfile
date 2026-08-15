FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/atachakki-0.0.1-SNAPSHOT.jar /app/atachakki-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "atachakki-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]