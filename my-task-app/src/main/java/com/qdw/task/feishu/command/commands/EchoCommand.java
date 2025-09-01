package com.qdw.task.feishu.command.commands;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.stereotype.Component;

/**
 * 回显命令实现
 * 将用户输入的内容原样返回
 */
@Component
public class EchoCommand implements FeishuTaskCommand {
    
    @Override
    public String getCommandName() {
        return "echo";
    }
    
    @Override
    public String getDescription() {
        return "回显输入的内容";
    }
    
    @Override
    public String getUsage() {
        return "echo <内容>";
    }
    
    // 注意：这个命令没有启用即时响应功能，默认返回false
    
    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            
            // 解析消息内容
            String textContent = parseMessageContent(content);
            
            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();
            
            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 2);
            String args = parts.length > 1 ? parts[1] : "";
            
            if (args.isEmpty()) {
                return "用法: echo <内容>";
            }
            
            return "您输入的内容是: " + args;
        } catch (Exception e) {
            return "处理回显命令时发生错误: " + e.getMessage();
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
