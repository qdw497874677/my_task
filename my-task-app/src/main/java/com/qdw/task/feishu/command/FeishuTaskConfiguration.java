package com.qdw.task.feishu.command;

import com.qdw.task.feishu.command.commands.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书任务命令配置类
 * 用于自动注册所有可用的命令
 */
@Configuration
public class FeishuTaskConfiguration {
    
    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;
    
    @Autowired(required = false)
    private AiCommand aiCommand;
    
    @Autowired(required = false)
    private RagAiCommand ragAiCommand;
    
    @Autowired(required = false)
    private UploadRagCommand uploadRagCommand;
    
    @Autowired(required = false)
    private HelpCommand helpCommand;
    
    @Autowired(required = false)
    private EchoCommand echoCommand;
    
    /**
     * 初始化命令注册
     */
    @PostConstruct
    public void initCommands() {
        // 注册AI命令
        if (aiCommand != null) {
            commandRegistry.registerCommand(aiCommand);
        }
        
        // 注册RAG AI命令
        if (ragAiCommand != null) {
            commandRegistry.registerCommand(ragAiCommand);
        }
        
        // 注册上传RAG文档命令
        if (uploadRagCommand != null) {
            commandRegistry.registerCommand(uploadRagCommand);
        }
        
        // 注册帮助命令
        if (helpCommand != null) {
            commandRegistry.registerCommand(helpCommand);
        }
        
        // 注册回显命令
        if (echoCommand != null) {
            commandRegistry.registerCommand(echoCommand);
        }
    }
}
