FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /hotel
COPY . .

RUN mvn clean package -pl main -am -DskipTests

FROM openjdk:21

WORKDIR /application
COPY --from=build /hotel/main/target/*.jar app.jar

WORKDIR /application
CMD ["java", "-jar", "app.jar"]

EXPOSE 8080
