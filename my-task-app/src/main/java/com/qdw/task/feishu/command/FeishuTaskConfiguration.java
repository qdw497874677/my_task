package com.qdw.task.feishu.command;

import com.qdw.task.feishu.command.commands.AiCommand;
import com.qdw.task.feishu.command.commands.EchoCommand;
import com.qdw.task.feishu.command.commands.HelpCommand;
import com.qdw.task.feishu.command.commands.RagAiCommand;
import com.qdw.task.feishu.command.commands.StatusCommand;
import com.qdw.task.feishu.command.commands.UploadRagCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 飞书任务配置类
 * 用于自动注册命令
 */
@Configuration
public class FeishuTaskConfiguration {
    
    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;
    
    @Autowired
    private HelpCommand helpCommand;
    
    @Autowired
    private EchoCommand echoCommand;
    
    @Autowired
    private StatusCommand statusCommand;
    
    @Autowired
    private AiCommand aiCommand;
    
    @Autowired
    private RagAiCommand ragAiCommand;
    
    @Autowired
    private UploadRagCommand uploadRagCommand;
    
    @PostConstruct
    public void registerCommands() {
        // 注册内置命令
        commandRegistry.registerCommand(helpCommand);
        commandRegistry.registerCommand(echoCommand);
        commandRegistry.registerCommand(statusCommand);
        commandRegistry.registerCommand(aiCommand);
        commandRegistry.registerCommand(ragAiCommand);
        commandRegistry.registerCommand(uploadRagCommand);
        
        System.out.println("Registered " + commandRegistry.getAllCommands().size() + " commands");
    }
}
