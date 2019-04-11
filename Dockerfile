FROM openjdk:11
WORKDIR /
ADD target/db-exporter-0.0.2.jar //
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/db-exporter-0.0.2.jar"]