# Use an official OpenJDK runtime for Java 23
FROM eclipse-temurin:23-jdk-alpine

# Set the working directory
WORKDIR /app

# Install necessary dependencies
RUN apk add --no-cache curl

# Copy the jar file
COPY target/gemsflare-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port (ensure this matches your Spring Boot settings)
EXPOSE 8443

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]