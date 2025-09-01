# 图像处理功能使用说明

## 功能概述
本功能基于Gemini 2.5 Flash Image Preview模型实现图像处理能力，支持图像生成、编辑和多轮对话。

## API接口

### 处理图像
- **URL**: `/api/image/process`
- **方法**: POST
- **参数**:
  - `image`: 图像文件（multipart/form-data）
  - `prompt`: 提示词
- **返回**: 处理结果

## 使用示例

### Java代码示例
```java
@Autowired
private IAiService imageAiService;

public void processImageExample() {
    // 获取API密钥（从配置文件或环境变量中获取）
    // String apiKey = System.getenv("OPENROUTER_API_KEY");
    
    // 图像数据（base64编码）
    String base64Image = "base64_encoded_image_data";
    
    // 提示词
    String prompt = "描述这张图片的内容";
    
    // 调用图像处理服务
    ChatResponse response = imageAiService.processImage("gemini-2.5-flash-image-preview", base64Image, prompt);
}
```

### cURL示例
```bash
curl -X POST https://openrouter.ai/api/v1/chat/completions \
  -H "Authorization: Bearer $OPENROUTER_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
  "model": "google/gemini-2.5-flash-image-preview:free",
  "messages": [
    {
      "role": "user",
      "content": [
        {
          "type": "text",
          "text": "What is in this image?"
        },
        {
          "type": "image_url",
          "image_url": {
            "url": "data:image/png;base64,iVBORw0KG..."
          }
        }
      ]
    }
  ]
}'
```

## 配置要求
1. 需要设置环境变量 `OPENROUTE_API_KEY` 或在配置文件中设置 `spring.ai.openroute.api-key`
2. 确保项目中包含Jackson依赖以支持JSON解析

## 生产环境部署

### 使用Docker Compose启动（推荐）
1. 复制 `.env.example` 文件为 `.env` 并填写相应的配置值：
   ```bash
   cp .env.example .env
   ```
   
2. 编辑 `.env` 文件，填写实际的配置值：
   ```bash
   # OpenRouter API密钥（用于图像处理功能）
   OPENROUTE_API_KEY=your_actual_openroute_api_key
   ```

3. 使用docker-compose启动服务：
   ```bash
   docker-compose up -d
   ```

### 配置文件说明
项目支持多种环境配置：
- `application.yml` - 默认配置
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

通过设置 `SPRING_PROFILES_ACTIVE` 环境变量来指定使用的配置文件：
- 开发环境：`SPRING_PROFILES_ACTIVE=dev`
- 生产环境：`SPRING_PROFILES_ACTIVE=prod`

## 支持的模型
- `google/gemini-2.5-flash-image-preview:free`

## 注意事项
1. 图像文件大小应适中，过大的文件可能影响处理速度
2. 提示词应清晰明确，以便获得更好的处理结果
3. 处理结果可能包含base64编码的图像数据，需要正确解析