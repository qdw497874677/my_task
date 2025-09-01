package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.stereotype.Component;

/**
 * 状态命令实现
 * 显示系统状态信息
 */
@Component
public class StatusCommand implements FeishuTaskCommand {
    
    @Override
    public String getCommandName() {
        return "status";
    }
    
    @Override
    public String getDescription() {
        return "显示系统状态信息";
    }
    
    @Override
    public String getUsage() {
        return null; // 无参数
    }
    
    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取发送者信息
            String senderName = event.getEvent().getSender().getSenderType();
            String senderId = event.getEvent().getSender().getSenderId().toString();
            
            // 构建状态信息
            StringBuilder statusInfo = new StringBuilder();
            statusInfo.append("系统状态报告:\n");
            statusInfo.append("- 发送者类型: ").append(senderName).append("\n");
            statusInfo.append("- 发送者ID: ").append(senderId).append("\n");
            statusInfo.append("- 消息类型: ").append(event.getEvent().getMessage().getMessageType()).append("\n");
            statusInfo.append("- 系统运行正常\n");
            
            return statusInfo.toString();
        } catch (Exception e) {
            return "获取状态信息时发生错误: " + e.getMessage();
        }
    }
}
