package com.qdw.task.feishu.command.commands;

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
    public String execute(P2MessageReceiveV1 event) {
        return commandRegistry.generateHelpMessage();
    }
}
