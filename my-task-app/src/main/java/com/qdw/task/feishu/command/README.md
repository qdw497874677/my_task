# 飞书任务命令系统

## 功能概述
飞书任务命令系统提供了一种灵活的方式来处理飞书消息并执行相应的业务逻辑。系统支持即时响应功能，可以在命令匹配后立即返回提示信息给用户，提升用户体验。

## 核心组件

### 1. FeishuTaskCommand接口
所有命令都需要实现此接口，它定义了命令的基本行为：

```java
public interface FeishuTaskCommand {
    // 获取命令名称
    String getCommandName();

    // 获取命令描述
    String getDescription();

    // 获取命令使用示例（可选）
    default String getUsage() {
        return null;
    }

    // 是否启用即时响应功能（可选，默认false）
    default boolean isInstantResponseEnabled() {
        return false;
    }

    // 获取即时响应消息（可选）
    default String getInstantResponseMessage() {
        return "正在处理您的请求，请稍候...";
    }

    // 执行命令
    String execute(P2MessageReceiveV1 event);
}
```

### 2. 即时响应功能
即时响应功能允许命令在匹配后立即返回提示信息给用户，让用户知道系统正在处理其请求。

#### 启用即时响应
要启用即时响应功能，只需在命令类中重写`isInstantResponseEnabled()`方法并返回`true`：

```java
@Override
public boolean isInstantResponseEnabled() {
    return true;
}
```

#### 自定义即时响应消息
可以通过重写`getInstantResponseMessage()`方法来自定义提示信息：

```java
@Override
public String getInstantResponseMessage() {
    return "正在处理您的请求，请稍候...";
}
```

### 3. 消息处理工具类
`FeishuMessageUtils` 提供了飞书消息处理的工具方法：

```java
// 解析消息内容中的文本
String textContent = FeishuMessageUtils.parseMessageContent(content);

// 过滤飞书@机器人标识
textContent = textContent.replaceAll("@_user_\\d+", "").trim();

// 获取图片的image_key
String imageKey = FeishuMessageUtils.getImageKey(content);
```

### 4. 默认启用即时响应的命令
以下命令默认启用了即时响应功能：
- `AiCommand` - AI命令
- `RagAiCommand` - RAG AI命令
- `UploadRagCommand` - 上传RAG文档命令
- `ImageCommand` - 图像处理命令
- `SrtCommand` - 字幕处理命令
- `TimeCommand` - 时间相关命令

### 5. 未启用即时响应的命令
以下命令未启用即时响应功能（默认行为）：
- `EchoCommand` - 回显命令
- `HelpCommand` - 帮助命令
- `StatusCommand` - 状态查询命令

## 命令实现示例

### 1. AI命令 (AiCommand)
最基础的AI对话命令：

```java
@Component
public class AiCommand implements FeishuTaskCommand {

    @Autowired
    private IAiService aiService;

    @Override
    public String getCommandName() {
        return "ai";
    }

    @Override
    public String getDescription() {
        return "调用 AI 服务生成响应";
    }

    @Override
    public String getUsage() {
        return "ai <问题内容>";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🤖 正在调用AI服务处理您的问题，预计需要10-30秒...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();

            // 解析消息内容
            String textContent = FeishuMessageUtils.parseMessageContent(content);

            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();

            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 2);
            String args = parts.length > 1 ? parts[1] : "";

            if (args.isEmpty()) {
                return "用法: ai <问题内容>";
            }

            // 调用 AI 服务生成响应
            ChatResponse response = aiService.generate("gpt-4o", args);

            // 提取响应内容
            if (response != null && response.getResult() != null) {
                return response.getResult().getOutput().getText();
            } else {
                return "AI 服务未返回有效响应";
            }
        } catch (Exception e) {
            return "处理 AI 命令时发生错误: " + e.getMessage();
        }
    }
}
```

### 2. RAG AI命令 (RagAiCommand)
结合知识库的AI命令：

```java
@Component
public class RagAiCommand implements FeishuTaskCommand {

    @Autowired
    private IAiService aiService;

    @Override
    public String getCommandName() {
        return "rag";
    }

    @Override
    public String getDescription() {
        return "调用 AI 服务结合 RAG 生成响应";
    }

    @Override
    public String getUsage() {
        return "rag <标签> <问题内容>";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🧠 正在调用AI服务结合RAG知识库处理您的问题，预计需要15-30秒...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();

            // 解析消息内容
            String textContent = FeishuMessageUtils.parseMessageContent(content);

            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();

            // 分离命令和参数 (格式: rag <tag> <问题>)
            String[] parts = textContent.split("\\s+", 3);

            if (parts.length < 3) {
                return "用法: rag <标签> <问题内容>\n例如: rag tech 请解释什么是人工智能?";
            }

            String ragTag = parts[1];
            String question = parts[2];

            if (ragTag.isEmpty() || question.isEmpty()) {
                return "用法: rag <标签> <问题内容>\n例如: rag tech 请解释什么是人工智能?";
            }

            // 调用 AI 服务结合 RAG 生成响应
            ChatResponse response = aiService.generateRag("gpt-4o", ragTag, question);

            // 提取响应内容
            if (response != null && response.getResult() != null) {
                return response.getResult().getOutput().getText();
            } else {
                return "AI 服务未返回有效响应";
            }
        } catch (Exception e) {
            return "处理 RAG AI 命令时发生错误: " + e.getMessage();
        }
    }
}
```

### 3. 图像处理命令 (ImageCommand)
支持图片分析和处理的命令：

```java
@Component
@Slf4j
public class ImageCommand implements FeishuTaskCommand {

    @Autowired(required = false)
    private IAiImageService aiImageService;

    @Override
    public String getCommandName() {
        return "image";
    }

    @Override
    public String getDescription() {
        return "AI图像分析和处理";
    }

    @Override
    public String getUsage() {
        return "image [提示词]";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🖼️ 正在分析图片内容，预计需要10-20秒...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();

            // 解析消息内容获取文本部分
            String textContent = FeishuMessageUtils.parseMessageContent(content);

            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();

            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 2);
            String prompt = parts.length > 1 ? parts[1] : "请描述这张图片的内容";

            if (aiImageService == null) {
                return "AI图像处理服务未启用，请检查配置";
            }

            // 获取图片的image_key
            String imageKey = FeishuMessageUtils.getImageKey(content);
            if (imageKey == null || imageKey.isEmpty()) {
                return "未找到图片，请直接发送图片或在图片后使用此命令";
            }

            log.info("Processing image with image_key: {}, prompt: {}", imageKey, prompt);

            // 构建图片下载URL
            String imageUrl = "https://open.feishu.cn/open-apis/im/v1/images/" + imageKey + "/read";

            // 调用AI图像处理服务
            String result = aiImageService.processImageByUrl("gemini-2.5-flash-image-preview", imageUrl, prompt);

            if (result != null && !result.isEmpty()) {
                return "🎨 图像分析结果：\n" + result;
            } else {
                return "图像处理失败，请重试";
            }

        } catch (Exception e) {
            log.error("Error executing image command", e);
            return "处理图像时发生错误: " + e.getMessage();
        }
    }
}
```

### 4. 创建新的命令类
```java
@Component
public class MyCommand implements FeishuTaskCommand {

    @Override
    public String getCommandName() {
        return "mycommand";
    }

    @Override
    public String getDescription() {
        return "我的自定义命令";
    }

    @Override
    public String getUsage() {
        return "mycommand <参数>";
    }

    // 启用即时响应功能
    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    // 自定义即时响应消息
    @Override
    public String getInstantResponseMessage() {
        return "正在执行我的自定义命令，请稍候...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        // 实现命令逻辑
        return "命令执行完成";
    }
}
```

## 最佳实践和注意事项

### 1. 命令命名规范
- 使用小写字母命名命令
- 命令名称应该简洁明了，易于记忆
- 避免使用特殊字符和空格

### 2. 参数处理模式
- **简单参数**：使用 `split("\\s+", 2)` 分离命令和参数
- **多参数**：使用 `split("\\s+", n)` 分离多个参数（n为参数数量+1）
- **可选参数**：提供默认值，如 `String prompt = parts.length > 1 ? parts[1] : "默认值"`

### 3. 错误处理
- 所有可能抛出异常的代码都应该用try-catch包裹
- 提供清晰的错误信息，帮助用户理解问题
- 对于服务不可用的情况，提供明确的提示

### 4. 依赖注入
- 使用 `@Autowired` 注入所需服务
- 对于可选服务，使用 `@Autowired(required = false)` 避免启动失败
- 在命令执行时检查服务是否可用

### 5. 即时响应使用场景
- **需要等待**：AI调用、文件处理、网络请求等耗时操作
- **不需要等待**：简单的计算、状态查询、帮助信息等

### 6. 日志记录
- 对于复杂操作，使用 `@Slf4j` 注解和日志记录
- 记录关键操作和错误信息，便于调试
- 避免在日志中记录敏感信息

### 7. 性能考虑
- 长时间运行的操作应该启用即时响应
- 合理设置超时时间，避免长时间等待
- 对于大文件处理，考虑分片或异步处理

### 8. 安全考虑
- 验证输入参数，避免注入攻击
- 对于文件上传，检查文件类型和大小
- 避免在响应中暴露系统内部信息

## 命令帮助信息
使用`help`命令可以查看所有可用命令及其状态：

```
help
```

启用即时响应的命令会在帮助信息中显示`[即时响应]`标记。

## 配置
命令会自动注册到系统中，无需手动配置。新创建的命令类只需添加`@Component`注解即可自动注册。
