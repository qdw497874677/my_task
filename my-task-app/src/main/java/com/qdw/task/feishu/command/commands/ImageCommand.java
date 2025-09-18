package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.domain.ai.image.IAiImageService;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 图像处理命令实现
 * 支持AI分析和处理用户发送的图片
 */
@Component
@Slf4j
public class ImageCommand implements FeishuTaskCommand {

    @Autowired(required = false)
    private IAiImageService aiImageService;

    @Override
    public String getCommandName() {
        return "image";
    }

    @Override
    public String getDescription() {
        return "AI图像分析和处理";
    }

    @Override
    public String getUsage() {
        return "image [提示词]";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🖼️ 正在分析图片内容，预计需要10-20秒...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();

            // 解析消息内容获取文本部分
            String textContent = FeishuMessageUtils.parseMessageContent(content);

            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();

            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 2);
            String prompt = parts.length > 1 ? parts[1] : "请描述这张图片的内容";

            if (aiImageService == null) {
                return "AI图像处理服务未启用，请检查配置";
            }

            // 获取图片的image_key
            String imageKey = FeishuMessageUtils.getImageKey(content);
            if (imageKey == null || imageKey.isEmpty()) {
                return "未找到图片，请直接发送图片或在图片后使用此命令";
            }

            log.info("Processing image with image_key: {}, prompt: {}", imageKey, prompt);

            // 构建图片下载URL
            String imageUrl = "https://open.feishu.cn/open-apis/im/v1/images/" + imageKey + "/read";

            // 调用AI图像处理服务
            String result = aiImageService.processImageByUrl("gemini-2.5-flash-image-preview", imageUrl, prompt);

            if (result != null && !result.isEmpty()) {
                return "🎨 图像分析结果：\n" + result;
            } else {
                return "图像处理失败，请重试";
            }

        } catch (Exception e) {
            log.error("Error executing image command", e);
            return "处理图像时发生错误: " + e.getMessage();
        }
    }
}