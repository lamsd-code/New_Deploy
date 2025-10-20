# ========== Build stage ==========
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Tối ưu cache phụ thuộc
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./
RUN mvn -q -DskipTests dependency:go-offline

# Copy source và build
COPY src/ src/
RUN mvn -q clean package -DskipTests

# ========== Run stage ==========
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV TZ=Asia/Ho_Chi_Minh \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Ho_Chi_Minh"

# Chạy bằng user không phải root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 👇 Không hard-code tên: lấy file JAR duy nhất trong target/
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
