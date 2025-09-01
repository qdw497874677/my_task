package com.qdw.task.feishu.command;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.api.feishu.IFeishuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 飞书消息处理器
 * 负责解析消息内容并分发到相应的命令处理器
 */
@Slf4j
@Component
public class FeishuMessageProcessor {
    
    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;
    
    @Autowired
    private IFeishuService feishuService;
    
    /**
     * 处理飞书消息
     * @param event 飞书消息事件
     */
    public void processMessage(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            log.info("Received message content: {}", content);
            
            // 解析消息内容
            String textContent = parseMessageContent(content);
            
            // 获取发送者信息
            String senderId = event.getEvent().getSender().getSenderId().getUserId();
            String senderType = event.getEvent().getSender().getSenderType();
            String messageId = event.getEvent().getMessage().getMessageId();

            // 处理消息并获取响应
            String response = handleCommand(textContent, event);
            
            // 发送响应消息
            if (response != null && !response.isEmpty()) {
//                sendResponse(response, senderId);
                replyMessage(response, messageId, null);
            }

        } catch (Exception e) {
            log.error("Error processing message", e);
            
            // 获取发送者信息
            String senderId = "2ed1a7aa"; // 默认发送给固定用户
            sendResponse("处理消息时发生错误: " + e.getMessage(), senderId);
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
            log.warn("Failed to parse message content as JSON, returning raw content: {}", content);
            return content;
        }
    }
    
    /**
     * 处理命令
     * @param textContent 文本内容
     * @param event 消息事件
     * @return 响应内容
     */
    private String handleCommand(String textContent, P2MessageReceiveV1 event) {
        if (textContent == null || textContent.trim().isEmpty()) {
            return "消息内容为空";
        }
        
        // 去除首尾空格
        textContent = textContent.trim();
        
        // 过滤掉飞书@机器人的标识（如@_user_1）
        textContent = textContent.replaceAll("@_user_\\d+", "").trim();
        
        // 检查是否是帮助命令
        if (textContent.equals("help") || textContent.equals("帮助")) {
            return commandRegistry.generateHelpMessage();
        }
        
        // 分离命令和参数
        String[] parts = textContent.split("\\s+", 2);
        String commandName = parts[0];
        
        // 查找对应的命令处理器
        log.info("Received command: {}", commandName);
        FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        if (command == null) {
            return "未知命令: " + commandName + "\n" + commandRegistry.generateHelpMessage();
        }
        
        // 执行命令
        return command.execute(event);
    }
    
    /**
     * 发送响应消息
     * @param response 响应内容
     * @param receiveId 接收者ID
     * @param receiveIdType 接收者ID类型
     */
    private void sendResponse(String response, String receiveId, String receiveIdType) {
        try {
            feishuService.sendMsg(response, receiveId, receiveIdType);
        } catch (Exception e) {
            log.error("Failed to send response message", e);
        }
    }

    private void replyMessage(String response, String messageId, String receiveIdType) {
        try {
            feishuService.replyMessage(response, messageId, receiveIdType);
        } catch (Exception e) {
            log.error("Failed to send response message", e);
        }
    }
    
    /**
     * 发送响应消息（默认发送给用户）
     * @param response 响应内容
     * @param receiveId 接收者ID
     */
    private void sendResponse(String response, String receiveId) {
        sendResponse(response, receiveId, "user_id");
    }
    
    /**
     * 发送响应消息（默认发送给固定用户）
     * @param response 响应内容
     */
    private void sendResponse(String response) {
        sendResponse(response, "2ed1a7aa", "user_id");
    }
}
