# Use official Maven image to build the application
FROM maven:3.9.9-amazoncorretto-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Use Amazon Corretto JRE for runtime (smaller image)
FROM amazoncorretto:17-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar blog_application-0.0.1-SNAPSHOT.jar

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "blog_application-0.0.1-SNAPSHOT.jar"]







































