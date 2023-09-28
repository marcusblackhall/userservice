# container for java 17 with spring boot app userservice
FROM eclipse-temurin:17
VOLUME /tmp

EXPOSE 8080
COPY target/userservice-0.0.1-SNAPSHOT.jar userservice.jar
ENTRYPOINT ["java","-jar","/userservice.jar"]