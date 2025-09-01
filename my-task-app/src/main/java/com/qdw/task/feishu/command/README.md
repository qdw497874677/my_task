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

### 3. 默认启用即时响应的命令
以下命令默认启用了即时响应功能：
- `AiCommand` - AI命令
- `RagAiCommand` - RAG AI命令
- `UploadRagCommand` - 上传RAG文档命令

### 4. 未启用即时响应的命令
以下命令未启用即时响应功能（默认行为）：
- `EchoCommand` - 回显命令
- `HelpCommand` - 帮助命令

## 使用示例

### 创建新的命令类
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

### 命令帮助信息
使用`help`命令可以查看所有可用命令及其状态：

```
help
```

启用即时响应的命令会在帮助信息中显示`[即时响应]`标记。

## 配置
命令会自动注册到系统中，无需手动配置。新创建的命令类只需添加`@Component`注解即可自动注册。
