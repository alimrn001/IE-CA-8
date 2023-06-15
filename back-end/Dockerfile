FROM openjdk:11

WORKDIR /app

COPY . /app

RUN apt-get update && apt-get install -y maven

RUN mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "target/myapp.jar"]