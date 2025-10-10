package com.qdw.task.feishu.command;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.feishu.exception.FeishuMessageExceptionHandler;
import com.qdw.task.feishu.message.cache.MessageCacheService;
import com.qdw.task.feishu.message.handler.MessageHandlerFactory;
import com.qdw.task.feishu.message.sender.MessageSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 飞书消息处理器
 * 重构后的版本使用策略模式和依赖注入，提高可维护性和扩展性
 */
@Slf4j
@Component
public class FeishuMessageProcessor {

    @Autowired
    private MessageHandlerFactory messageHandlerFactory;

    @Autowired
    private MessageCacheService messageCacheService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private FeishuMessageExceptionHandler exceptionHandler;

    @Value("${feishu.default-user-id:user_for_error_notifications}")
    private String defaultUserId;

    /**
     * 处理飞书消息
     * @param event 飞书消息事件
     */
    public void processMessage(P2MessageReceiveV1 event) {
        try {
            String messageId = event.getEvent().getMessage().getMessageId();

            // 检查是否已经处理过该消息
            if (messageCacheService.isProcessed(messageId)) {
                log.info("Message already processed, skipping: {}", messageId);
                return;
            }

            // 标记消息为已处理
            messageCacheService.markProcessed(messageId);

            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            log.info("Received message content: {}", content);

            // 获取消息类型
            String messageType = FeishuMessageUtils.getMessageType(content);
            log.info("Message type: {}", messageType);

            // 如果是未知消息类型，输出调试信息
            if ("unknown".equals(messageType)) {
                log.warn("Unknown message type detected. Raw content: {}", content);
                // 尝试解析消息内容以进行调试
                try {
                    com.alibaba.fastjson.JSONObject jsonContent = com.alibaba.fastjson.JSONObject.parseObject(content);
                    log.warn("Parsed JSON keys: {}", jsonContent.keySet());
                    if (jsonContent.containsKey("content")) {
                        Object contentObj = jsonContent.get("content");
                        log.warn("Content object type: {}", contentObj.getClass().getName());
                        log.warn("Content object value: {}", contentObj);
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse content as JSON for debugging", e);
                }
            }

            // 获取发送者和聊天信息
            String senderId = event.getEvent().getSender().getSenderId().getUserId();
            String chatId = event.getEvent().getMessage().getChatId();
            String chatType = event.getEvent().getMessage().getChatType();

            log.info("Chat type: {}, Chat ID: {}, Sender ID: {}", chatType, chatId, senderId);

            // 处理消息
            String response = processMessageByType(messageType, content, event);

            // 发送响应消息
            if (response != null && !response.isEmpty()) {
                log.info("Sending response for chat type: {}, senderId: {}, messageId: {}", chatType, senderId, messageId);
                messageSenderService.sendResponseByChatType(response, chatType, chatId, senderId, messageId);
            } else {
                log.info("No response to send for message: {}", messageId);
            }

        } catch (Exception e) {
            // 尝试处理特定异常
            if (!exceptionHandler.handleSpecificException(e, "FeishuMessageProcessor.processMessage")) {
                // 如果不是特定异常，使用通用异常处理
                exceptionHandler.handleMessageException(e, "FeishuMessageProcessor.processMessage");
            }
        }
    }

    /**
     * 根据消息类型处理消息
     * @param messageType 消息类型
     * @param content 消息内容
     * @param event 飞书消息事件
     * @return 处理结果
     */
    private String processMessageByType(String messageType, String content, P2MessageReceiveV1 event) {
        // 特殊处理复合消息类型
        if ("post_with_image".equals(messageType)) {
            messageType = "image";
        }

        // 获取对应的处理器
        var handler = messageHandlerFactory.getHandler(messageType);
        if (handler != null) {
            return handler.handleMessage(event, content);
        }

        // 如果没有找到处理器，默认作为文本处理
        log.warn("No handler found for message type: {}, defaulting to text processing", messageType);
        var textHandler = messageHandlerFactory.getHandler("text");
        if (textHandler != null) {
            return textHandler.handleMessage(event, content);
        }

        return "不支持的消息类型: " + messageType;
    }
}