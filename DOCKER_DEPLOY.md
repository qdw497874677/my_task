# Docker部署说明

## 目录结构
```
my-task/
├── Dockerfile              # 应用构建文件
├── docker-compose.yml      # 生产环境部署配置
├── docker-compose-dev.yml  # 开发环境依赖服务配置
├── .env.example           # 环境变量模板
└── dev-ops/               # 已有的开发环境配置
```

## 部署方式

### 1. 生产环境部署

1. 使用构建脚本构建应用和Docker镜像：
   ```bash
   ./build.sh
   ```
   
   或者手动构建应用jar包：
   ```bash
   mvn clean package -DskipTests
   ```

2. 复制 `.env.example` 到 `.env` 并填写实际配置：
   ```bash
   cp .env.example .env
   # 编辑 .env 文件填入实际配置
   ```

3. 启动所有服务：
   ```bash
   docker-compose up -d
   ```
   
进启动当前应用
```bash
docker-compose -f docker-compose-my-task.yml up -d
```

### 2. 开发环境部署

1. 仅启动依赖服务（数据库、Redis等）：
   ```bash
   docker-compose -f docker-compose-dev.yml up -d
   ```

2. 在本地运行应用：
   ```bash
   cd start
   mvn spring-boot:run
   ```

## 服务访问地址

- 应用服务: http://localhost:8080
- Redis管理界面: http://localhost:18081 (用户名/密码: admin/admin)
- PostgreSQL数据库: localhost:15432
- Ollama服务: http://localhost:11434

## 环境变量说明

| 变量名 | 说明 | 示例 |
|-------|------|------|
| FEISHU_APPID | 飞书应用ID | cli_xxxxxxxxxx |
| FEISHU_APPSECRET | 飞书应用密钥 | xxxxxxxxxxxxxxxx |
| OPENAI_BASE_URL | OpenAI API地址 | https://api.openai.com/v1 |
| OPENAI_API_KEY | OpenAI API密钥 | sk-xxxxxxxxxxxxxxxx |
| ZHIPUAI_API_KEY | 智谱AI API密钥 | xxxxxxxxxxxxxxxx |

## 注意事项

1. 首次启动时，Ollama需要拉取模型，请执行：
   ```bash
   docker exec -it ollama ollama pull nomic-embed-text
   ```

2. 如果需要使用其他AI模型，可以在Ollama容器中拉取：
   ```bash
   docker exec -it ollama ollama pull deepseek-r1:1.5b
   ```

3. 数据库和Redis数据会持久化存储在Docker卷中，删除容器不会丢失数据。