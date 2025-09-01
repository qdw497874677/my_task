package com.qdw.task.feishu.command.commands;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.domain.ai.IRAGService;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上传 RAG 文档命令实现
 * 支持通过文本内容上传 RAG 文档
 */
@Component
public class UploadRagCommand implements FeishuTaskCommand {
    
    @Autowired
    private IRAGService ragService;
    
    @Override
    public String getCommandName() {
        return "uploadrag";
    }
    
    @Override
    public String getDescription() {
        return "上传 RAG 文档";
    }
    
    @Override
    public String getUsage() {
        return "uploadrag <标签> <文档内容>";
    }
    
    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }
    
    @Override
    public String getInstantResponseMessage() {
        return "正在上传RAG文档，请稍候...";
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
            
            // 分离命令和参数 (格式: uploadrag <tag> <content>)
            String[] parts = textContent.split("\\s+", 3);
            
            if (parts.length < 3) {
                return "用法: uploadrag <标签> <文档内容>\n例如: uploadrag tech 人工智能是计算机科学的一个分支...";
            }
            
            String ragTag = parts[1];
            String documentContent = parts[2];
            
            if (ragTag.isEmpty() || documentContent.isEmpty()) {
                return "用法: uploadrag <标签> <文档内容>\n例如: uploadrag tech 人工智能是计算机科学的一个分支...";
            }
            
            // 调用 RAG 服务上传文档
            String result = ragService.uploadDocument(ragTag, documentContent);
            
            return result != null ? result : "文档上传成功";
        } catch (Exception e) {
            return "处理上传 RAG 文档命令时发生错误: " + e.getMessage();
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