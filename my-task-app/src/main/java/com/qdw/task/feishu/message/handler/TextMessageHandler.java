package com.qdw.task.feishu.message.handler;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.domain.ai.IAiService;
import com.qdw.task.feishu.command.FeishuTaskCommandRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文本消息处理器
 */
@Slf4j
@Component
public class TextMessageHandler implements MessageHandler {

    @Autowired(required = false)
    private IAiService aiService;

    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;

    @Value("${feishu.greetings:你好,您好,hi,hello,嗨,在吗,在干嘛,早上好,下午好,晚上好}")
    private String[] greetings;

    @Value("${feishu.ai.model:anthropic/claude-3.5-sonnet}")
    private String aiModel;

    @Override
    public String getSupportedMessageType() {
        return "text";
    }

    @Override
    public String handleMessage(P2MessageReceiveV1 event, String content) {
        String textContent = FeishuMessageUtils.parseMessageContent(content);
        String chatType = event.getEvent().getMessage().getChatType();
        String senderId = event.getEvent().getSender().getSenderId().getUserId();

        if (textContent == null || textContent.trim().isEmpty()) {
            return "消息内容为空";
        }

        textContent = textContent.trim();

        // 私聊中检查问候语
        if ("p2p".equals(chatType) && isGreeting(textContent)) {
            return handlePrivateGreeting(event);
        }

        // 过滤掉飞书@机器人的标识
        textContent = textContent.replaceAll("@_user_\\d+", "").trim();

        // 检查是否是帮助命令
        if (textContent.equals("help") || textContent.equals("帮助")) {
            return generateHelpMessage();
        }

        // 处理群聊命令
        if ("group".equals(chatType)) {
            return handleGroupCommand(textContent, event);
        }

        // 私聊中，如果不是命令则尝试AI对话
        if ("p2p".equals(chatType)) {
            return handlePrivateAiChat(textContent, event);
        }

        return "未知消息类型\n\n" + commandRegistry.generateHelpMessage();
    }

    private boolean isGreeting(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase().trim();
        for (String greeting : greetings) {
            if (lowerText.equals(greeting.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String handlePrivateGreeting(P2MessageReceiveV1 event) {
        String senderId = event.getEvent().getSender().getSenderId().getUserId();
        log.info("Handling private greeting from user: {}", senderId);

        StringBuilder response = new StringBuilder();
        response.append("你好！很高兴与你私聊 🤖\n\n");
        response.append("我是AI助手，可以为你提供以下服务：\n");
        response.append("• AI对话：发送消息给我，我会用AI回复你\n");
        response.append("• 图片分析：发送图片，我会为你分析内容\n");
        response.append("• 任务处理：我可以帮你处理各种任务\n\n");
        response.append("发送 'help' 查看所有可用命令，或者直接告诉我你想做什么！");

        return response.toString();
    }

    private String handlePrivateAiChat(String message, P2MessageReceiveV1 event) {
        String senderId = event.getEvent().getSender().getSenderId().getUserId();
        log.info("Processing private AI chat from user: {}, message: {}", senderId, message);

        if (aiService == null) {
            return "抱歉，AI服务暂时不可用，请稍后再试。你可以发送 'help' 查看我能为你做什么。";
        }

        try {
            StringBuilder context = new StringBuilder();
            context.append("你是一个友好的AI助手，正在与用户进行私聊。");
            context.append("请用自然、友好的语调回复用户的问题。");
            context.append("如果用户问的是技术问题，提供准确的技术答案。");
            context.append("如果用户只是聊天，保持轻松友好的对话风格。");
            context.append("用户的问题是：").append(message);

            ChatResponse aiResponse = aiService.generate(aiModel, context.toString());

            if (aiResponse != null && aiResponse.getResult() != null) {
                return aiResponse.getResult().getOutput().getText();
            } else {
                return "AI 服务未返回有效响应";
            }

        } catch (Exception e) {
            log.error("Error processing private AI chat", e);
            return "抱歉，处理你的消息时出现了问题，请稍后再试。";
        }
    }

    private String handleGroupCommand(String textContent, P2MessageReceiveV1 event) {
        log.info("Processing group command: {}", textContent);

        // 分离命令和参数
        String[] parts = textContent.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();

        // 获取对应的命令
        com.qdw.task.feishu.command.FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        if (command != null) {
            log.info("Executing group command: {}", commandName);
            return command.execute(event);
        }

        // 如果没有找到命令，返回完整帮助信息
        return "未知命令 '" + commandName + "'\n\n" + commandRegistry.generateHelpMessage();
    }

    private String generateHelpMessage() {
        return commandRegistry.generateHelpMessage();
    }
}