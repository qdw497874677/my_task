# 使用官方OpenJDK运行时作为基础镜像
FROM openjdk:17-jdk-slim

# 设置维护者信息
LABEL maintainer="quandawei"

# 设置工作目录
WORKDIR /app

# 复制Maven构建的jar包到容器中
# 这里假设您的应用打包后会生成一个可执行的jar文件
COPY start/target/start-*.jar app.jar

# 暴露应用端口
# 根据application.yml配置，Spring Boot默认使用8080端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]