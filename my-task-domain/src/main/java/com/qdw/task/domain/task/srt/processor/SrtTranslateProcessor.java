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
 * SRT翻译处理器
 *
 * 功能：对校准后的英文内容进行AI翻译，生成中英双语字幕
 * 输入状态：已英文校准
 * 输出状态：已翻译
 */
@Slf4j
@Service
public class SrtTranslateProcessor implements TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> {

    @Autowired
    private com.qdw.task.domain.ai.IAiService aiService; // 假设存在AI服务接口

    @Override
    public ProcessResult<SrtFlowStatus, SrtTaskContext> process(SrtTask task) {
        log.info("开始SRT翻译，任务ID: {}", task.getTaskId());

        try {
            SrtTaskContext context = task.getContext();
            if (context == null || context.getSrtChunks() == null) {
                throw new IllegalArgumentException("任务上下文或字幕块不能为空");
            }

            List<SrtTaskContext.SrtChunk> chunks = context.getSrtChunks();
            int totalChunks = chunks.size();
            int processedChunks = 0;

            log.info("需要翻译 {} 个字幕块", totalChunks);

            // 翻译每个字幕块
            for (SrtTaskContext.SrtChunk chunk : chunks) {
                try {
                    log.debug("翻译字幕块 {}: 序号 {}-{}",
                            chunk.getChunkId(), chunk.getStartSequence(), chunk.getEndSequence());

                    // 使用AI进行翻译
                    String translatedContent = translateWithAi(chunk.getCalibratedContent());

                    // 更新块的翻译内容
                    chunk.setTranslatedContent(translatedContent);
                    processedChunks++;

                    // 更新进度
                    updateProgress(context, processedChunks, totalChunks, "翻译中");

                } catch (Exception e) {
                    log.warn("翻译字幕块 {} 失败: {}", chunk.getChunkId(), e.getMessage());
                    // 如果翻译失败，使用校准后的内容
                    chunk.setTranslatedContent(chunk.getCalibratedContent());
                }
            }

            // 合并所有翻译后的内容
            StringBuilder translatedBuilder = new StringBuilder();
            for (SrtTaskContext.SrtChunk chunk : chunks) {
                if (chunk.getTranslatedContent() != null) {
                    translatedBuilder.append(chunk.getTranslatedContent()).append("\n\n");
                }
            }
            context.setTranslatedContent(translatedBuilder.toString().trim());

            log.info("SRT翻译完成，共处理 {} 个块", processedChunks);

            return new ProcessResult<>(nextStatus(), context);

        } catch (Exception e) {
            log.error("SRT翻译失败", e);
            SrtTaskContext errorContext = task.getContext();
            if (errorContext != null && errorContext.getProgressInfo() != null) {
                errorContext.getProgressInfo().setCurrentPhase("翻译失败: " + e.getMessage());
            }
            return new ProcessResult<>(SrtFlowStatus.异常, task.getContext());
        }
    }

    /**
     * 使用AI进行翻译
     */
    private String translateWithAi(String srtContent) {
        if (aiService == null) {
            log.warn("AI服务未配置，跳过翻译");
            return srtContent;
        }

        try {
            // 构建翻译提示词
            String prompt = buildTranslationPrompt(srtContent);

            // 调用AI服务
            String aiResponse = callAiService(prompt);

            // 解析AI响应，提取翻译后的SRT内容
            return extractTranslatedSrt(aiResponse);

        } catch (Exception e) {
            log.warn("AI翻译失败，使用原始内容: {}", e.getMessage());
            return srtContent;
        }
    }

    /**
     * 构建翻译提示词
     */
    private String buildTranslationPrompt(String srtContent) {
        return String.format("""
            请将以下英文SRT字幕翻译成中英双语格式：

            %s

            翻译要求：
            1. 保持原有的时间轴和序号不变
            2. 每条字幕翻译为：英文原文 + 中文翻译
            3. 中文翻译要准确、自然
            4. 保持专业术语的一致性
            5. 保持口语化表达的自然流畅
            6. 时间戳格式保持不变（HH:MM:SS,mmm --> HH:MM:SS,mmm）

            输出格式示例：
            1
            00:00:01,000 --> 00:00:03,000
            Hello World
            你好世界

            2
            00:00:04,000 --> 00:00:06,000
            This is a test
            这是一个测试

            请返回完整的中英双语SRT格式内容，不要包含其他解释。
            """, srtContent);
    }

    /**
     * 调用AI服务
     */
    private String callAiService(String prompt) {
        // 这里调用AI服务，目前为Mock实现
        // 实际项目中应该调用真实的AI服务
        log.debug("调用AI服务进行SRT翻译，提示词长度: {}", prompt.length());

        // Mock: 返回翻译示例
        return "AI翻译完成";
    }

    /**
     * 从AI响应中提取翻译后的SRT内容
     */
    private String extractTranslatedSrt(String aiResponse) {
        // 这里应该从AI响应中提取翻译后的SRT内容
        // 目前为Mock实现
        log.debug("从AI响应中提取翻译后的SRT内容");
        return "翻译后的中英双语SRT内容";
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
            // 翻译阶段占50%-90%的进度
            double progress = 50.0 + (40.0 * processed / total);
            context.getProgressInfo().setCompletionPercentage(progress);
        }
    }

    @Override
    public SrtFlowStatus getStatus() {
        return SrtFlowStatus.已英文校准;
    }

    @Override
    public SrtFlowStatus nextStatus() {
        return SrtFlowStatus.已翻译;
    }
}