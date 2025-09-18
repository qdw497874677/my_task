package com.qdw.task.domain.task.srt;

import com.qdw.task.domain.task.base.BaseTask;

/**
 * SRT字幕翻译任务
 *
 * 功能：将英文SRT字幕文件翻译为中英双语字幕
 * 流程：
 * 1. 已创建 -> 已切分：将SRT文件按100条字幕切分为多个块
 * 2. 已切分 -> 已英文校准：对每个分块通过AI进行英文内容校准
 * 3. 已英文校准 -> 已翻译：对校准后的内容进行AI翻译
 * 4. 已翻译 -> 已完成：合并所有翻译结果，生成最终字幕文件
 */
public class SrtTask extends BaseTask<SrtFlowStatus, SrtTaskContext> {

    @Override
    public SrtFlowStatus getCurStatus() {
        return getStatus();
    }

    @Override
    public SrtFlowStatus getCreated() {
        return SrtFlowStatus.已创建;
    }

    @Override
    public SrtFlowStatus getCompleted() {
        return SrtFlowStatus.已完成;
    }

    @Override
    public SrtFlowStatus getErrorStatus() {
        return SrtFlowStatus.异常;
    }

    /**
     * 获取任务描述
     */
    public String getTaskDescription() {
        return "SRT字幕翻译任务";
    }

    /**
     * 获取当前进度
     */
    public double getProgress() {
        if (getContext() == null || getContext().getProgressInfo() == null) {
            return 0.0;
        }
        return getContext().getProgressInfo().getCompletionPercentage();
    }

    /**
     * 获取当前处理阶段
     */
    public String getCurrentPhase() {
        if (getContext() == null || getContext().getProgressInfo() == null) {
            return "未开始";
        }
        return getContext().getProgressInfo().getCurrentPhase();
    }

    /**
     * 获取总字幕条数
     */
    public int getTotalSubtitles() {
        if (getContext() == null || getContext().getProgressInfo() == null) {
            return 0;
        }
        return getContext().getProgressInfo().getTotalSubtitles();
    }

    /**
     * 获取已处理条数
     */
    public int getProcessedCount() {
        if (getContext() == null || getContext().getProgressInfo() == null) {
            return 0;
        }
        return getContext().getProgressInfo().getProcessedCount();
    }
}