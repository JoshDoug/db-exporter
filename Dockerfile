FROM openjdk:11
WORKDIR /
ADD target/db-exporter-0.0.1-SNAPSHOT.jar //
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/db-exporter-0.0.1-SNAPSHOT.jar"]