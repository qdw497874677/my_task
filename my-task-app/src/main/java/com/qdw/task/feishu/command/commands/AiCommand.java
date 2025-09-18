package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.domain.ai.IAiService;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI 命令实现
 * 调用 AI 服务生成响应
 */
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