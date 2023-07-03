## Stage 1: Build
#FROM maven:3.8.4-openjdk-17 AS MAVEN
#MAINTAINER MEHRANI
#COPY pom.xml /build/
#COPY src /build/src/
#WORKDIR /build/
#RUN mvn package
#
## Stage 2: Package and run
#FROM openjdk:17
#WORKDIR /app
#COPY --from=MAVEN /build/target/*.jar /app/app.jar
#EXPOSE 8888
#ENTRYPOINT ["java", "-jar", "app.jar"]