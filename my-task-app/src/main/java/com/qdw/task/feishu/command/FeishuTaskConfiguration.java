package com.qdw.task.feishu.command;

import com.qdw.task.feishu.command.commands.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 飞书任务命令配置类
 * 用于自动注册所有可用的命令
 */
@Configuration
@EnableAsync
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

    @Autowired(required = false)
    private TimeCommand timeCommand;

    @Autowired(required = false)
    private ImageCommand imageCommand;

    @Autowired(required = false)
    private SrtCommand srtCommand;
    
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

        // 注册时间命令
        if (timeCommand != null) {
            commandRegistry.registerCommand(timeCommand);
        }

        // 注册图像处理命令
        if (imageCommand != null) {
            commandRegistry.registerCommand(imageCommand);
        }

        // 注册SRT翻译命令
        if (srtCommand != null) {
            commandRegistry.registerCommand(srtCommand);
        }
    }
}
