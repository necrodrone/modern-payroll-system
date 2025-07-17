# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jre

# Set working directory inside the container
WORKDIR /app

# Copy the jar file into the container
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port your app runs on (default 8080 or 8081)
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
