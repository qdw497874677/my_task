package com.qdw.task.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

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
            JSONObject contentJson = JSONObject.parseObject(content);

            // 如果是简单的文本消息
            if (contentJson.containsKey("text")) {
                return contentJson.getString("text");
            }

            // 如果是post类型消息，需要从content中提取文本
            if (contentJson.containsKey("content")) {
                Object contentObj = contentJson.get("content");
                if (contentObj instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) contentObj;
                    StringBuilder textBuilder = new StringBuilder();

                    // 遍历内容块，查找文本元素
                    for (Object block : contentList) {
                        if (block instanceof java.util.List) {
                            java.util.List<?> elements = (java.util.List<?>) block;
                            for (Object element : elements) {
                                if (element instanceof JSONObject) {
                                    JSONObject elementJson = (JSONObject) element;
                                    if ("text".equals(elementJson.getString("tag"))) {
                                        String text = elementJson.getString("text");
                                        if (text != null) {
                                            textBuilder.append(text);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return textBuilder.toString().trim();
                }
            }

            return "";
        } catch (Exception e) {
            log.warn("Failed to parse message content as JSON, returning raw content: {}", content);
            return content;
        }
    }

    /**
     * 获取消息类型
     * @param content 原始消息内容
     * @return 消息类型 (text, image, file, post等)
     */
    public static String getMessageType(String content) {
        try {
            JSONObject contentJson = JSONObject.parseObject(content);

            if (contentJson.containsKey("text")) {
                return "text";
            } else if (contentJson.containsKey("image_key")) {
                return "image";
            } else if (contentJson.containsKey("file_key")) {
                return "file";
            } else if (contentJson.containsKey("content")) {
                // 检查是否是post类型消息
                Object contentObj = contentJson.get("content");
                if (contentObj instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) contentObj;
                    // 遍历内容块，检查是否包含图片
                    for (Object block : contentList) {
                        if (block instanceof java.util.List) {
                            java.util.List<?> elements = (java.util.List<?>) block;
                            for (Object element : elements) {
                                if (element instanceof JSONObject) {
                                    JSONObject elementJson = (JSONObject) element;
                                    if ("img".equals(elementJson.getString("tag"))) {
                                        return "post_with_image";
                                    }
                                }
                            }
                        }
                    }
                    return "post";
                }
                return "unknown";
            } else {
                return "unknown";
            }
        } catch (Exception e) {
            log.warn("Failed to parse message type from content: {}", content);
            return "unknown";
        }
    }

    /**
     * 获取图片消息的image_key
     * @param content 原始消息内容
     * @return 图片的image_key，如果不是图片消息则返回null
     */
    public static String getImageKey(String content) {
        try {
            JSONObject contentJson = JSONObject.parseObject(content);

            // 如果是简单的图片消息
            if (contentJson.containsKey("image_key")) {
                return contentJson.getString("image_key");
            }

            // 如果是post类型消息，需要从content中提取
            if (contentJson.containsKey("content")) {
                Object contentObj = contentJson.get("content");
                if (contentObj instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) contentObj;
                    // 遍历内容块，查找图片元素
                    for (Object block : contentList) {
                        if (block instanceof java.util.List) {
                            java.util.List<?> elements = (java.util.List<?>) block;
                            for (Object element : elements) {
                                if (element instanceof JSONObject) {
                                    JSONObject elementJson = (JSONObject) element;
                                    if ("img".equals(elementJson.getString("tag"))) {
                                        return elementJson.getString("image_key");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("Failed to parse image_key from content: {}", content);
            return null;
        }
    }

    /**
     * 获取文件消息的file_key
     * @param content 原始消息内容
     * @return 文件的file_key，如果不是文件消息则返回null
     */
    public static String getFileKey(String content) {
        try {
            JSONObject contentJson = JSONObject.parseObject(content);
            return contentJson.getString("file_key");
        } catch (Exception e) {
            log.warn("Failed to parse file_key from content: {}", content);
            return null;
        }
    }

    /**
     * 获取文件信息
     * @param content 原始消息内容
     * @return 文件信息Map，包含file_key, file_name, file_type等
     */
    public static Map<String, Object> getFileInfo(String content) {
        try {
            JSONObject contentJson = JSONObject.parseObject(content);
            return (Map<String, Object>) contentJson.get("file_info");
        } catch (Exception e) {
            log.warn("Failed to parse file_info from content: {}", content);
            return null;
        }
    }
}