package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间命令实现
 * 返回当前系统时间
 */
@Component
public class TimeCommand implements FeishuTaskCommand {

    @Override
    public String getCommandName() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "返回当前系统时间";
    }

    @Override
    public String getUsage() {
        return "time";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            // 获取当前系统时间
            LocalDateTime now = LocalDateTime.now();

            // 格式化时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
            String formattedTime = now.format(formatter);

            return "当前系统时间: " + formattedTime;
        } catch (Exception e) {
            return "获取时间时发生错误: " + e.getMessage();
        }
    }
}