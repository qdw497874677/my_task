package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.domain.task.srt.SrtTask;
import com.qdw.task.domain.task.srt.SrtTaskContext;
import com.qdw.task.domain.task.srt.SrtTaskService;
import com.qdw.task.domain.task.srt.gateway.SrtTaskGateway;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * SRT字幕翻译命令
 *
 * 功能：处理SRT字幕文件的翻译任务
 * 使用方法：
 * - /srt create [文件URL] - 创建翻译任务
 * - /srt status [任务ID] - 查询任务状态
 * - /srt list - 列出所有任务
 * - /srt help - 显示帮助信息
 */
@Component
@Slf4j
public class SrtCommand implements FeishuTaskCommand {

    @Autowired(required = false)
    private SrtTaskService srtTaskService;

    @Autowired(required = false)
    private SrtTaskGateway srtTaskGateway;

    @Override
    public String getCommandName() {
        return "srt";
    }

    @Override
    public String getDescription() {
        return "SRT字幕翻译任务管理";
    }

    @Override
    public String getUsage() {
        return "srt [create|status|list|help] [参数]";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🎬 正在处理SRT翻译任务，预计需要30-60秒...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            if (srtTaskService == null || srtTaskGateway == null) {
                return "SRT翻译服务未启用，请检查配置";
            }

            // 获取消息内容
            String content = event.getEvent().getMessage().getContent();
            String textContent = FeishuMessageUtils.parseMessageContent(content);

            // 过滤掉飞书@机器人的标识
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();

            // 分离命令和参数
            String[] parts = textContent.split("\\s+", 3);
            if (parts.length < 2) {
                return getHelpMessage();
            }

            String subCommand = parts[1].toLowerCase();

            switch (subCommand) {
                case "create":
                    return handleCreateCommand(parts);
                case "status":
                    return handleStatusCommand(parts);
                case "list":
                    return handleListCommand();
                case "help":
                    return getHelpMessage();
                default:
                    return "未知命令: " + subCommand + "\n" + getHelpMessage();
            }

        } catch (Exception e) {
            log.error("执行SRT命令时发生错误", e);
            return "处理SRT命令时发生错误: " + e.getMessage();
        }
    }

    /**
     * 处理创建任务命令
     */
    private String handleCreateCommand(String[] parts) {
        if (parts.length < 3) {
            return "请提供SRT文件URL\n用法: /srt create [文件URL]";
        }

        String fileUrl = parts[2];
        log.info("创建SRT翻译任务，文件URL: {}", fileUrl);

        try {
            // 这里应该是从文件URL获取SRT内容
            // 目前为Mock实现
            String mockSrtContent = getMockSrtContent();

            // 创建任务
            SrtTask task = srtTaskService.createTask(fileUrl, mockSrtContent);

            // 保存任务
            srtTaskGateway.save(task);

            // 处理任务
            task = srtTaskService.processTaskToCompletion(task);

            // 更新保存最终状态
            srtTaskGateway.save(task);

            return String.format("""
                ✅ SRT翻译任务创建并处理完成！

                📋 任务信息：
                • 任务ID：%s
                • 文件URL：%s
                • 最终状态：%s
                • 处理进度：%.1f%%
                • 总字幕条数：%d

                💡 使用 `/srt status %s` 查询详细进度
                """, task.getTaskId(), fileUrl, task.getStatus(), task.getProgress() * 100,
                    task.getTotalSubtitles(), task.getTaskId());

        } catch (Exception e) {
            log.error("创建SRT任务失败", e);
            return "创建SRT任务失败: " + e.getMessage();
        }
    }

    /**
     * 处理状态查询命令
     */
    private String handleStatusCommand(String[] parts) {
        if (parts.length < 3) {
            return "请提供任务ID\n用法: /srt status [任务ID]";
        }

        String taskId = parts[2];
        log.info("查询SRT任务状态，任务ID: {}", taskId);

        Optional<SrtTask> taskOpt = srtTaskGateway.findById(taskId);
        if (!taskOpt.isPresent()) {
            return "未找到任务ID: " + taskId;
        }

        SrtTask task = taskOpt.get();
        SrtTaskContext.ProgressInfo progress = task.getContext() != null ?
                task.getContext().getProgressInfo() : null;

        return String.format("""
            📊 SRT翻译任务状态

            🆔 任务ID：%s
            📝 状态描述：%s
            🎯 当前进度：%.1f%%
            📊 当前阶段：%s
            📝 总字幕条数：%d
            ✅ 已处理条数：%d

            %s
            """, task.getTaskId(), srtTaskService.getTaskStatusDescription(task),
                task.getProgress() * 100,
                progress != null ? progress.getCurrentPhase() : "未知",
                task.getTotalSubtitles(),
                task.getProcessedCount(),
                task.getErrorMsg() != null ? "⚠️ 错误信息：" + task.getErrorMsg() : "");
    }

    /**
     * 处理列表命令
     */
    private String handleListCommand() {
        log.info("查询所有SRT任务");

        var allTasks = srtTaskGateway.findAll();
        if (allTasks.isEmpty()) {
            return "当前没有SRT翻译任务";
        }

        StringBuilder response = new StringBuilder();
        response.append("📋 SRT翻译任务列表\n\n");

        allTasks.values().forEach(task -> {
            response.append(String.format("""
                🆔 %s
                • 状态：%s
                • 进度：%.1f%%
                • 阶段：%s

                """, task.getTaskId(), task.getStatus(), task.getProgress() * 100,
                    task.getCurrentPhase()));
        });

        // 添加统计信息
        var stats = srtTaskGateway.getStatistics();
        response.append(String.format("""
            📈 统计信息：
            • 总任务数：%d
            • 平均进度：%.1f%%

            """, stats.get("total"), stats.get("averageProgress")));

        response.append("💡 使用 `/srt status [任务ID]` 查询详细信息");

        return response.toString();
    }

    /**
     * 获取帮助信息
     */
    private String getHelpMessage() {
        return """
            📖 SRT字幕翻译命令帮助

            🎯 功能：将英文SRT字幕文件翻译为中英双语字幕

            📝 使用方法：
            • `/srt create [文件URL]` - 创建翻译任务
            • `/srt status [任务ID]` - 查询任务状态
            • `/srt list` - 列出所有任务
            • `/srt help` - 显示此帮助信息

            🔄 处理流程：
            1. 文件切分 → 2. 英文校准 → 3. AI翻译 → 4. 完成

            💡 提示：
            • 支持标准SRT格式文件
            • 自动按100条字幕切分处理
            • 使用AI进行英文校准和中文翻译
            • 保持原有时间轴不变
            """;
    }

    /**
     * 获取Mock SRT内容（用于测试）
     */
    private String getMockSrtContent() {
        return """
            1
            00:00:01,000 --> 00:00:03,500
            Hello and welcome to our presentation

            2
            00:00:04,000 --> 00:00:06,500
            Today we'll be discussing artificial intelligence

            3
            00:00:07,000 --> 00:00:09,500
            AI is transforming many industries

            4
            00:00:10,000 --> 00:00:12,500
            From healthcare to finance

            5
            00:00:13,000 --> 00:00:15,500
            The possibilities are endless

            6
            00:00:16,000 --> 00:00:18,500
            Let's explore some key applications

            7
            00:00:19,000 --> 00:00:21,500
            Machine learning algorithms

            8
            00:00:22,000 --> 00:00:24,500
            Natural language processing

            9
            00:00:25,000 --> 00:00:27,500
            Computer vision systems

            10
            00:00:28,000 --> 00:00:30,500
            These technologies are changing our world
            """;
    }
}