# syntax=docker/dockerfile:1
############################################
# 1) Build stage
############################################
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# 先只拷贝 POM，预下载依赖，加速构建
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY users-service/pom.xml users-service/pom.xml
COPY orders-service/pom.xml orders-service/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
# 如果有公共模块（例如 common/），也补一行：
# COPY common/pom.xml common/pom.xml

# 预拉依赖（不跑测试）
RUN mvn -q -DskipTests dependency:go-offline

# 再拷贝全部源码
COPY . .

# 选择要构建的子模块（默认 users-service）
ARG MODULE=users-service
# -pl 指定模块，-am 自动构建依赖的模块；不跑测试
RUN mvn -q -DskipTests -pl ${MODULE} -am package

############################################
# 2) Runtime stage
############################################
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 与上面的 MODULE 保持一致（build 时会继承 ARG 值）
ARG MODULE=users-service

# 复制构建产物（Spring Boot 可执行 JAR）
# 如果你的版本号不是 SNAPSHOT，通配 *.jar 也可以
COPY --from=build /workspace/${MODULE}/target/*.jar /app/app.jar

# 容器内 JVM 调优（避免吃满容器内存）
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=20 -XX:+UseContainerSupport"

# Spring Boot 默认端口 8080；如果你的模块在 application.yml 里改了端口，也没问题
ENTRYPOINT ["java","-jar","/app/app.jar"]
