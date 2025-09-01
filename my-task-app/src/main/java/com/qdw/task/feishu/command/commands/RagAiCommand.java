package com.qdw.task.feishu.command.commands;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.domain.ai.IAiService;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RAG AI 命令实现
 * 调用 AI 服务结合 RAG 生成响应
 */
@Component
public class RagAiCommand implements FeishuTaskCommand {
    
    @Autowired
    private IAiService aiService;
    
    @Override
    public String getCommandName() {
        return "rag";
    }
    
    @Override
    public String getDescription() {
        return "调用 AI 服务结合 RAG 生成响应";
    }
    
    @Override
    public String getUsage() {
        return "rag <标签> <问题内容>";
    }
    
    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }
    
    @Override
    public String getInstantResponseMessage() {
        return "正在调用AI服务结合RAG知识库处理您的问题，请稍候...";
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
            
            // 分离命令和参数 (格式: rag <tag> <问题>)
            String[] parts = textContent.split("\\s+", 3);
            
            if (parts.length < 3) {
                return "用法: rag <标签> <问题内容>\n例如: rag tech 请解释什么是人工智能?";
            }
            
            String ragTag = parts[1];
            String question = parts[2];
            
            if (ragTag.isEmpty() || question.isEmpty()) {
                return "用法: rag <标签> <问题内容>\n例如: rag tech 请解释什么是人工智能?";
            }
            
            // 调用 AI 服务结合 RAG 生成响应
            ChatResponse response = aiService.generateRag("gpt-4o", ragTag, question);
            
            // 提取响应内容
            if (response != null && response.getResult() != null) {
                return response.getResult().getOutput().getText();
            } else {
                return "AI 服务未返回有效响应";
            }
        } catch (Exception e) {
            return "处理 RAG AI 命令时发生错误: " + e.getMessage();
        }
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