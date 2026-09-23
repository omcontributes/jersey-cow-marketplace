# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user to run the app (security best practice)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]