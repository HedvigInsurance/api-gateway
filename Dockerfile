FROM openjdk:8


ADD target/api-gateway-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar api-gateway-0.0.1-SNAPSHOT.jar
