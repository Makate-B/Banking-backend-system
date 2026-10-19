FROM eclipse-temurin:21-jdk

WORKDIR /build

COPY target/bank-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]