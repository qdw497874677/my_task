#!/bin/bash

# 构建脚本用于打包和构建Docker镜像

# 设置变量
APP_NAME="my-task-app"
VERSION="1.0.0-SNAPSHOT"

echo "开始构建项目..."

# 清理并构建项目
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Maven构建失败!"
    exit 1
fi

echo "Maven构建成功!"

# 构建Docker镜像
echo "构建Docker镜像..."
docker build -t $APP_NAME:$VERSION .

if [ $? -ne 0 ]; then
    echo "Docker镜像构建失败!"
    exit 1
fi

echo "Docker镜像构建成功: $APP_NAME:$VERSION"

echo "构建完成!"