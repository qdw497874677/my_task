package com.qdw.task.feishu.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 飞书任务命令注册中心
 * 用于注册和管理所有可用的命令
 */
@Slf4j
@Component
public class FeishuTaskCommandRegistry {
    
    private final Map<String, FeishuTaskCommand> commandMap = new ConcurrentHashMap<>();
    
    /**
     * 注册命令
     * @param command 命令实例
     */
    public void registerCommand(FeishuTaskCommand command) {
        String commandName = command.getCommandName().toLowerCase();
        commandMap.put(commandName, command);
        log.info("Registered command: {}, total commands: {}", commandName, commandMap.size());
    }
    
    /**
     * 根据命令名称获取命令
     * @param commandName 命令名称
     * @return 命令实例，如果不存在则返回null
     */
    public FeishuTaskCommand getCommand(String commandName) {
        return commandMap.get(commandName.toLowerCase());
    }
    
    /**
     * 获取所有已注册的命令
     * @return 命令映射
     */
    public Map<String, FeishuTaskCommand> getAllCommands() {
        return commandMap;
    }
    
    /**
     * 生成帮助信息
     * @return 帮助信息字符串
     */
    public String generateHelpMessage() {
        StringBuilder helpMessage = new StringBuilder("可用命令列表：\n");
        for (FeishuTaskCommand command : commandMap.values()) {
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

        if (commandMap.isEmpty()) {
            helpMessage.append("（暂无可用命令）\n");
        }

        return helpMessage.toString();
    }
}
