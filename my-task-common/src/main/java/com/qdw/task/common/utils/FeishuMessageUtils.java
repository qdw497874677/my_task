package com.qdw.task.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 飞书消息工具类
 * 提供飞书消息解析等通用功能
 */
@Slf4j
public class FeishuMessageUtils {

    /**
     * 解析飞书消息内容
     * @param content 原始消息内容
     * @return 解析后的文本内容
     */
    public static String parseMessageContent(String content) {
        try {
            // 飞书文本消息内容是JSON格式: {"text":"具体文本内容"}
            JSONObject contentJson = JSONObject.parseObject(content);
            return contentJson.getString("text");
        } catch (Exception e) {
            log.warn("Failed to parse message content as JSON, returning raw content: {}", content);
            return content;
        }
    }
}