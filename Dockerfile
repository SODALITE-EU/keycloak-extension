FROM maven:3-openjdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM jboss/keycloak
COPY --from=build /home/app/target/keycloak-1.0-SNAPSHOT.jar /opt/jboss/keycloak/standalone/deployments/keycloak-1.0-SNAPSHOT.jar
