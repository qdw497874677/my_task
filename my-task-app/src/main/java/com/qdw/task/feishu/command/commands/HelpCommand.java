package com.qdw.task.feishu.command.commands;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import com.qdw.task.feishu.command.FeishuTaskCommandRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 帮助命令实现
 * 显示所有可用命令的帮助信息
 */
@Component
public class HelpCommand implements FeishuTaskCommand {
    
    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;
    
    @Override
    public String getCommandName() {
        return "help";
    }
    
    @Override
    public String getDescription() {
        return "显示所有可用命令的帮助信息";
    }
    
    @Override
    public String getUsage() {
        return "help [命令名称]";
    }
    
    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            
            // 解析消息内容
            String textContent = parseMessageContent(content);
            
            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();
            
            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 2);
            String commandName = parts.length > 1 ? parts[1].toLowerCase() : "";
            
            if (commandName.isEmpty()) {
                // 显示所有命令的帮助信息
                return generateFullHelpMessage();
            } else {
                // 显示特定命令的帮助信息
                return generateCommandHelpMessage(commandName);
            }
        } catch (Exception e) {
            return "处理帮助命令时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 生成完整帮助信息
     * @return 完整帮助信息
     */
    private String generateFullHelpMessage() {
        StringBuilder helpMessage = new StringBuilder("可用命令列表：\n");
        
        for (FeishuTaskCommand command : commandRegistry.getAllCommands().values()) {
            helpMessage.append("- ").append(command.getCommandName())
                    .append(" : ").append(command.getDescription());
            
            // 添加使用示例（如果有的话）
            String usage = command.getUsage();
            if (usage != null && !usage.isEmpty()) {
                helpMessage.append(" (用法: ").append(usage).append(")");
            }
            
            // 显示即时响应功能状态
            if (command.isInstantResponseEnabled()) {
                helpMessage.append(" [即时响应]");
            }
            
            helpMessage.append("\n");
        }
        
        helpMessage.append("\n使用 'help <命令名称>' 获取特定命令的详细帮助信息");
        return helpMessage.toString();
    }
    
    /**
     * 生成特定命令的帮助信息
     * @param commandName 命令名称
     * @return 特定命令的帮助信息
     */
    private String generateCommandHelpMessage(String commandName) {
        FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        if (command == null) {
            return "未找到命令: " + commandName + "\n使用 'help' 查看所有可用命令";
        }
        
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("命令: ").append(command.getCommandName()).append("\n");
        helpMessage.append("描述: ").append(command.getDescription()).append("\n");
        
        String usage = command.getUsage();
        if (usage != null && !usage.isEmpty()) {
            helpMessage.append("用法: ").append(usage).append("\n");
        }
        
        // 显示即时响应功能状态
        if (command.isInstantResponseEnabled()) {
            helpMessage.append("即时响应: 已启用\n");
            helpMessage.append("提示信息: ").append(command.getInstantResponseMessage()).append("\n");
        } else {
            helpMessage.append("即时响应: 未启用\n");
        }
        
        return helpMessage.toString();
    }
    
    /**
     * 解析消息内容
     * @param content 原始消息内容
     * @return 解析后的文本内容
     */
    private String parseMessageContent(String content) {
        try {
            // 飞书文本消息内容是JSON格式: {"text":"具体文本内容"}
            JSONObject contentJson = JSONObject.parseObject(content);
            return contentJson.getString("text");
        } catch (Exception e) {
            return content;
        }
    }
}
