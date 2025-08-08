# Start with a base image for Java applications.
FROM eclipse-temurin:17-jre

# Set the working directory inside the container.
WORKDIR /app

# Copy the compiled JAR file from your target directory into the container.
# This assumes you have already run `./gradlew build` or `mvn package`.
COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which the Spring Boot application runs.
EXPOSE 8084

# Define the command to run the application when the container starts.
ENTRYPOINT ["java", "-jar", "notification-service.jar"]