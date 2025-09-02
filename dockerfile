# syntax=docker/dockerfile:1

############################################
# 1) Build stage
############################################
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# 先只拷贝 POM，预拉依赖（加速）
COPY pom.xml .
COPY api-gateway/pom.xml        api-gateway/pom.xml
COPY users-service/pom.xml      users-service/pom.xml
COPY orders-service/pom.xml     orders-service/pom.xml
COPY inventory-service/pom.xml  inventory-service/pom.xml
# 若有公共模块（如 common/），加一行：
# COPY common/pom.xml            common/pom.xml

RUN mvn -q -DskipTests dependency:go-offline

# 再拷贝全部源码
COPY . .

# 选择要构建的子模块（默认 users-service）
ARG MODULE=users-service

# 构建指定模块（含其依赖），不跑测试
RUN mvn -q -DskipTests -pl "${MODULE}" -am package

# 关键：挑选“可执行的 Boot JAR”，避开 plain/original/sources/javadoc
RUN set -eux; \
    jar_path="$(ls -1 ${MODULE}/target/*.jar | grep -Ev '(-plain|-original|-sources|-javadoc)\\.jar$' | head -n1)"; \
    echo "Selected boot JAR => ${jar_path}"; \
    test -n "${jar_path}"; \
    cp "${jar_path}" /workspace/app.jar

############################################
# 2) Runtime stage
############################################
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 复制已挑选好的可执行 JAR
COPY --from=build /workspace/app.jar /app/app.jar

# 容器内 JVM 调优
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=20 -XX:+UseContainerSupport"

# 如需声明端口可取消下一行注释（默认 8080；各模块可在应用配置里改）
# EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]