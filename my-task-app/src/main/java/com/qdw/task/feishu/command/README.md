# 飞书任务命令处理系统

## 概述

这是一个通用的飞书消息任务处理模板，用于处理飞书用户消息并将其转换为方法调用和结果返回。该系统提供了一个可扩展的命令框架，允许开发者轻松添加新的命令功能。

## 核心组件

### 1. FeishuTaskCommand (命令接口)
所有飞书任务命令都需要实现此接口：
- `getCommandName()`: 返回命令名称
- `getDescription()`: 返回命令描述
- `execute(P2MessageReceiveV1 event)`: 执行命令逻辑并返回结果

### 2. FeishuTaskCommandRegistry (命令注册中心)
负责管理所有已注册的命令：
- 自动注册带有 `@Component` 注解的命令实现
- 提供命令查找功能
- 生成帮助信息

### 3. FeishuMessageProcessor (消息处理器)
处理飞书消息的核心组件：
- 解析消息内容
- 分发到相应的命令处理器
- 发送响应消息

### 4. FeishuEventHandler (事件处理器)
飞书事件的入口点，接收并转发消息到消息处理器。

## 如何添加新命令

### 1. 创建命令类
```java
@Component
public class MyCommand implements FeishuTaskCommand {
    
    @Override
    public String getCommandName() {
        return "mycommand";
    }
    
    @Override
    public String getDescription() {
        return "这是一个示例命令";
    }
    
    @Override
    public String execute(P2MessageReceiveV1 event) {
        // 实现命令逻辑
        return "命令执行结果";
    }
}
```

### 2. 命令会自动注册
使用 `@Component` 注解的命令会自动被Spring容器管理并注册到命令注册中心。

## 内置命令

### 1. help/帮助
显示所有可用命令的帮助信息

### 2. echo
将用户输入的文本原样返回
用法: `echo <文本内容>`

### 3. status
显示系统状态信息

## 使用示例

用户在飞书中发送以下消息：
- `help` - 显示帮助信息
- `echo Hello World` - 返回 "Hello World"
- `status` - 显示系统状态

## 扩展功能

### 自定义消息发送
IFeishuService 接口提供了两种发送消息的方法：
- `sendMsg(String msg)` - 发送消息给默认用户
- `sendMsg(String msg, String receiveId, String receiveIdType)` - 发送消息给指定用户

## 测试

运行 `FeishuTaskCommandTest` 类来验证命令系统是否正常工作。
