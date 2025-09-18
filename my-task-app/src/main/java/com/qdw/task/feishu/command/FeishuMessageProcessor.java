package com.qdw.task.feishu.command;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.api.feishu.IFeishuService;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.domain.ai.image.IAiImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    @Autowired(required = false)
    private IAiImageService aiImageService;

    @Autowired
    private FeishuCommandExecutor commandExecutor;

    @Value("${feishu.default-user-id:user_for_error_notifications}")
    private String defaultUserId;

    // 用于存储已处理的消息ID，防止重复处理
    private final Set<String> processedMessageIds = ConcurrentHashMap.newKeySet();
    
    /**
     * 处理飞书消息
     * @param event 飞书消息事件
     */
    public void processMessage(P2MessageReceiveV1 event) {
        try {
            // 获取消息ID
            String messageId = event.getEvent().getMessage().getMessageId();

            // 检查是否已经处理过该消息
            if (processedMessageIds.contains(messageId)) {
                log.info("Message already processed, skipping: {}", messageId);
                return;
            }

            // 标记消息为已处理
            processedMessageIds.add(messageId);

            // 清理旧的消息ID（防止内存泄漏）
            if (processedMessageIds.size() > 1000) {
                processedMessageIds.clear();
                log.info("Cleared processed message IDs cache");
            }

            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            log.info("Received message content: {}", content);

            // 获取消息类型
            String messageType = FeishuMessageUtils.getMessageType(content);
            log.info("Message type: {}", messageType);

            // 获取发送者信息
            String senderId = event.getEvent().getSender().getSenderId().getUserId();
            String senderType = event.getEvent().getSender().getSenderType();

            String response = null;

            // 根据消息类型处理
            if ("image".equals(messageType) || "post_with_image".equals(messageType)) {
                response = handleImageMessage(content, event);
            } else if ("file".equals(messageType)) {
                response = handleFileMessage(content, event);
            } else if ("post".equals(messageType)) {
                // 处理post类型的文本消息
                String textContent = FeishuMessageUtils.parseMessageContent(content);
                response = handleCommand(textContent, event);
            } else {
                // 默认处理为文本消息
                String textContent = FeishuMessageUtils.parseMessageContent(content);
                response = handleCommand(textContent, event);
            }

            // 发送响应消息
            if (response != null && !response.isEmpty()) {
                replyMessage(response, messageId, null);
            }

        } catch (Exception e) {
            log.error("Error processing message", e);

            // 获取发送者信息
            String senderId = defaultUserId; // 默认发送给固定用户
            sendResponse("处理消息时发生错误: " + e.getMessage(), senderId);
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

        // 检查命令是否存在
        FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        if (command == null) {
            return "未知命令: " + commandName + "\n" + commandRegistry.generateHelpMessage();
        }

        // 如果启用即时响应，立即返回提示消息并异步执行命令
        if (commandExecutor.isInstantResponseEnabled(commandName)) {
            return handleAsyncCommand(commandName, event);
        }

        // 同步执行命令
        try {
            return command.execute(event);
        } catch (Exception e) {
            log.error("Error executing command: {}", commandName, e);
            return "执行命令时发生错误: " + e.getMessage();
        }
    }

    /**
     * 异步处理命令
     * 立即返回即时响应消息，异步执行命令并发送最终结果
     *
     * @param commandName 命令名称
     * @param event 消息事件
     * @return 即时响应消息
     */
    private String handleAsyncCommand(String commandName, P2MessageReceiveV1 event) {
        String messageId = event.getEvent().getMessage().getMessageId();
        String senderId = event.getEvent().getSender().getSenderId().getUserId();

        // 获取即时响应消息
        String instantResponse = commandExecutor.getInstantResponseMessage(commandName);

        // 异步执行命令并发送最终结果
        CompletableFuture<String> commandFuture = commandExecutor.executeCommandAsync(commandName, event, 30);

        commandFuture.thenAccept(result -> {
            try {
                // 异步发送最终结果
                sendResponse(result, senderId);
                log.info("Async command completed successfully: {}", commandName);
            } catch (Exception e) {
                log.error("Error sending async command result: {}", commandName, e);
            }
        }).exceptionally(throwable -> {
            try {
                // 发送错误消息
                String errorMessage = "命令执行失败: " + throwable.getMessage();
                sendResponse(errorMessage, senderId);
                log.error("Async command failed: {}", commandName, throwable);
            } catch (Exception e) {
                log.error("Error sending async command error: {}", commandName, e);
            }
            return null;
        });

        return instantResponse;
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
        sendResponse(response, defaultUserId, "user_id");
    }

    /**
     * 处理图片消息
     * @param content 消息内容
     * @param event 消息事件
     * @return 响应内容
     */
    private String handleImageMessage(String content, P2MessageReceiveV1 event) {
        try {
            String imageKey = FeishuMessageUtils.getImageKey(content);
            log.info("Received image with image_key: {}", imageKey);

            if (aiImageService == null) {
                return "AI图像处理服务未启用";
            }

            // 构建图片下载URL
            String imageUrl = "https://open.feishu.cn/open-apis/im/v1/images/" + imageKey + "/read";

            // 使用默认提示词处理图片
            String prompt = "请描述这张图片的内容";
            String result = aiImageService.processImageByUrl("gemini-2.5-flash-image-preview", imageUrl, prompt);

            return "图片分析结果：\n" + result;
        } catch (Exception e) {
            log.error("Error processing image message", e);
            return "处理图片时发生错误: " + e.getMessage();
        }
    }

    /**
     * 处理文件消息
     * @param content 消息内容
     * @param event 消息事件
     * @return 响应内容
     */
    private String handleFileMessage(String content, P2MessageReceiveV1 event) {
        try {
            String fileKey = FeishuMessageUtils.getFileKey(content);
            java.util.Map<String, Object> fileInfo = FeishuMessageUtils.getFileInfo(content);

            log.info("Received file with file_key: {}", fileKey);
            if (fileInfo != null) {
                log.info("File info: {}", fileInfo);
            }

            String fileName = fileInfo != null ? (String) fileInfo.get("file_name") : "未知文件";
            String fileSize = fileInfo != null ? String.valueOf(fileInfo.get("file_size")) : "未知大小";

            return "收到文件：" + fileName + "\n文件大小：" + fileSize + " bytes\n文件Key：" + fileKey + "\n\n文件处理功能正在开发中...";
        } catch (Exception e) {
            log.error("Error processing file message", e);
            return "处理文件时发生错误: " + e.getMessage();
        }
    }
}
