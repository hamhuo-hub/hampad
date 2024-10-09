# Step 1: Build the application
#FROM maven:3.8.1-jdk-11 AS build
#COPY . /home/app
#WORKDIR /home/app
#RUN mvn package -DskipTests

# Step 2: Use OpenJDK to run the application

FROM openjdk:11-jre-slim
WORKDIR /app
COPY jar/hampad-1.3-SNAPSHOT-jar-with-dependencies.jar /app/myapp.jar
EXPOSE 8080
CMD ["java", "-jar", "myapp.jar"]