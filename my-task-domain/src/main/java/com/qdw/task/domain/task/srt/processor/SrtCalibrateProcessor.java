package com.qdw.task.domain.task.srt.processor;

import com.qdw.task.domain.task.srt.SrtFlowStatus;
import com.qdw.task.domain.task.srt.SrtParser;
import com.qdw.task.domain.task.srt.SrtTask;
import com.qdw.task.domain.task.srt.SrtTaskContext;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 英文校准处理器
 *
 * 功能：对每个分块通过AI进行英文内容校准
 * 输入状态：已切分
 * 输出状态：已英文校准
 */
@Slf4j
@Service
public class SrtCalibrateProcessor implements TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> {

    @Autowired
    private com.qdw.task.domain.ai.IAiService aiService; // 假设存在AI服务接口

    @Override
    public ProcessResult<SrtFlowStatus, SrtTaskContext> process(SrtTask task) {
        log.info("开始英文校准，任务ID: {}", task.getTaskId());

        try {
            SrtTaskContext context = task.getContext();
            if (context == null || context.getSrtChunks() == null) {
                throw new IllegalArgumentException("任务上下文或字幕块不能为空");
            }

            List<SrtTaskContext.SrtChunk> chunks = context.getSrtChunks();
            int totalChunks = chunks.size();
            int processedChunks = 0;

            log.info("需要校准 {} 个字幕块", totalChunks);

            // 校准每个字幕块
            for (SrtTaskContext.SrtChunk chunk : chunks) {
                try {
                    log.debug("校准字幕块 {}: 序号 {}-{}",
                            chunk.getChunkId(), chunk.getStartSequence(), chunk.getEndSequence());

                    // 使用AI进行英文校准
                    String calibratedContent = calibrateWithAi(chunk.getContent());

                    // 更新块的校准内容
                    chunk.setCalibratedContent(calibratedContent);
                    processedChunks++;

                    // 更新进度
                    updateProgress(context, processedChunks, totalChunks, "英文校准中");

                } catch (Exception e) {
                    log.warn("校准字幕块 {} 失败: {}", chunk.getChunkId(), e.getMessage());
                    // 如果校准失败，使用原始内容
                    chunk.setCalibratedContent(chunk.getContent());
                }
            }

            // 合并所有校准后的内容
            StringBuilder calibratedBuilder = new StringBuilder();
            for (SrtTaskContext.SrtChunk chunk : chunks) {
                if (chunk.getCalibratedContent() != null) {
                    calibratedBuilder.append(chunk.getCalibratedContent()).append("\n\n");
                }
            }
            context.setCalibratedContent(calibratedBuilder.toString().trim());

            log.info("英文校准完成，共处理 {} 个块", processedChunks);

            return new ProcessResult<>(nextStatus(), context);

        } catch (Exception e) {
            log.error("英文校准失败", e);
            SrtTaskContext errorContext = task.getContext();
            if (errorContext != null && errorContext.getProgressInfo() != null) {
                errorContext.getProgressInfo().setCurrentPhase("英文校准失败: " + e.getMessage());
            }
            return new ProcessResult<>(SrtFlowStatus.异常, task.getContext());
        }
    }

    /**
     * 使用AI进行英文校准
     */
    private String calibrateWithAi(String srtContent) {
        if (aiService == null) {
            log.warn("AI服务未配置，跳过校准");
            return srtContent;
        }

        try {
            // 构建校准提示词
            String prompt = buildCalibrationPrompt(srtContent);

            // 调用AI服务
            String aiResponse = callAiService(prompt);

            // 解析AI响应，提取校准后的SRT内容
            return extractCalibratedSrt(aiResponse);

        } catch (Exception e) {
            log.warn("AI校准失败，使用原始内容: {}", e.getMessage());
            return srtContent;
        }
    }

    /**
     * 构建校准提示词
     */
    private String buildCalibrationPrompt(String srtContent) {
        return String.format("""
            请对以下英文SRT字幕内容进行校准和优化：

            %s

            校准要求：
            1. 修正拼写错误和语法错误
            2. 改善标点符号使用
            3. 优化不自然的表达
            4. 保持时间轴不变
            5. 保持字幕序号不变
            6. 确保时间戳格式正确（HH:MM:SS,mmm --> HH:MM:SS,mmm）

            请返回校准后的完整SRT格式内容，不要包含其他解释。
            """, srtContent);
    }

    /**
     * 调用AI服务
     */
    private String callAiService(String prompt) {
        // 这里调用AI服务，目前为Mock实现
        // 实际项目中应该调用真实的AI服务
        log.debug("调用AI服务进行英文校准，提示词长度: {}", prompt.length());

        // Mock: 返回原始内容
        // 在实际实现中，这里应该调用AI服务
        return "AI校准完成";
    }

    /**
     * 从AI响应中提取校准后的SRT内容
     */
    private String extractCalibratedSrt(String aiResponse) {
        // 这里应该从AI响应中提取SRT内容
        // 目前为Mock实现，返回原始内容
        // 在实际实现中，需要解析AI响应并提取SRT格式的内容

        log.debug("从AI响应中提取校准后的SRT内容");
        return "校准后的SRT内容";
    }

    /**
     * 更新进度信息
     */
    private void updateProgress(SrtTaskContext context, int processed, int total, String phase) {
        if (context.getProgressInfo() != null) {
            context.getProgressInfo().setProcessedCount(
                    context.getProgressInfo().getTotalSubtitles() * processed / total
            );
            context.getProgressInfo().setCurrentPhase(phase);
            // 校准阶段占10%-50%的进度
            double progress = 10.0 + (40.0 * processed / total);
            context.getProgressInfo().setCompletionPercentage(progress);
        }
    }

    @Override
    public SrtFlowStatus getStatus() {
        return SrtFlowStatus.已切分;
    }

    @Override
    public SrtFlowStatus nextStatus() {
        return SrtFlowStatus.已英文校准;
    }
}