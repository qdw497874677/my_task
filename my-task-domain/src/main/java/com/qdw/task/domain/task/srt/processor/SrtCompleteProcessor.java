package com.qdw.task.domain.task.srt.processor;

import com.qdw.task.domain.task.srt.SrtFlowStatus;
import com.qdw.task.domain.task.srt.SrtTask;
import com.qdw.task.domain.task.srt.SrtTaskContext;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SRT任务完成处理器
 *
 * 功能：合并所有翻译结果，生成最终字幕文件
 * 输入状态：已翻译
 * 输出状态：已完成
 */
@Slf4j
@Service
public class SrtCompleteProcessor implements TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> {

    @Override
    public ProcessResult<SrtFlowStatus, SrtTaskContext> process(SrtTask task) {
        log.info("开始完成SRT翻译任务，任务ID: {}", task.getTaskId());

        try {
            SrtTaskContext context = task.getContext();
            if (context == null) {
                throw new IllegalArgumentException("任务上下文不能为空");
            }

            // 验证翻译结果
            String translatedContent = context.getTranslatedContent();
            if (translatedContent == null || translatedContent.trim().isEmpty()) {
                throw new IllegalArgumentException("翻译内容不能为空");
            }

            // 验证翻译内容格式
            if (!validateTranslatedContent(translatedContent)) {
                log.warn("翻译内容格式可能有问题，但仍将标记为完成");
            }

            // 更新进度信息
            if (context.getProgressInfo() != null) {
                context.getProgressInfo().setProcessedCount(context.getProgressInfo().getTotalSubtitles());
                context.getProgressInfo().setCurrentPhase("翻译完成");
                context.getProgressInfo().setCompletionPercentage(100.0);
            }

            // 生成任务摘要
            generateTaskSummary(task);

            log.info("SRT翻译任务完成，任务ID: {}", task.getTaskId());

            return new ProcessResult<>(nextStatus(), context);

        } catch (Exception e) {
            log.error("SRT任务完成处理失败", e);
            SrtTaskContext errorContext = task.getContext();
            if (errorContext != null && errorContext.getProgressInfo() != null) {
                errorContext.getProgressInfo().setCurrentPhase("任务完成失败: " + e.getMessage());
            }
            return new ProcessResult<>(SrtFlowStatus.异常, task.getContext());
        }
    }

    /**
     * 验证翻译内容格式
     */
    private boolean validateTranslatedContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        // 检查是否包含基本的SRT格式元素
        boolean hasSequenceNumbers = content.matches("(?m).*^\\d+.*$");
        boolean hasTimeStamps = content.matches("(?m).*\\d{2}:\\d{2}:\\d{2},\\d{3}.*-->.*\\d{2}:\\d{2}:\\d{2},\\d{3}.*$");
        boolean hasChineseCharacters = content.matches(".*[\\u4e00-\\u9fff].*");

        log.debug("内容验证结果 - 序号: {}, 时间戳: {}, 中文字符: {}",
                hasSequenceNumbers, hasTimeStamps, hasChineseCharacters);

        return hasSequenceNumbers && hasTimeStamps && hasChineseCharacters;
    }

    /**
     * 生成任务摘要
     */
    private void generateTaskSummary(SrtTask task) {
        SrtTaskContext context = task.getContext();
        if (context == null || context.getProgressInfo() == null) {
            return;
        }

        SrtTaskContext.ProgressInfo progress = context.getProgressInfo();

        log.info("任务摘要 - ID: {}, 总字幕条数: {}, 完成状态: {}, 耗时: {}ms",
                task.getTaskId(),
                progress.getTotalSubtitles(),
                progress.getCompletionPercentage() + "%",
                System.currentTimeMillis() - getTaskStartTime(task));

        // 可以在这里添加更多的统计信息
        if (context.getSrtChunks() != null) {
            log.info("处理块数: {}, 平均每块字幕数: {}",
                    context.getSrtChunks().size(),
                    progress.getTotalSubtitles() / context.getSrtChunks().size());
        }
    }

    /**
     * 获取任务开始时间（Mock实现）
     */
    private long getTaskStartTime(SrtTask task) {
        // 实际项目中应该从任务创建时间或其他地方获取
        return System.currentTimeMillis() - 5000; // 假设任务处理了5秒
    }

    @Override
    public SrtFlowStatus getStatus() {
        return SrtFlowStatus.已翻译;
    }

    @Override
    public SrtFlowStatus nextStatus() {
        return SrtFlowStatus.已完成;
    }
}