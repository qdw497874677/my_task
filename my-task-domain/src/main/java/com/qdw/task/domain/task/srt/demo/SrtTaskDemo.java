package com.qdw.task.domain.task.srt.demo;

import com.qdw.task.domain.task.srt.*;
import com.qdw.task.domain.task.srt.gateway.SrtTaskGateway;
import com.qdw.task.domain.task.srt.processor.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SRT字幕翻译任务演示
 */
@Slf4j
@Component
public class SrtTaskDemo {

    @Autowired
    private SrtTaskGateway srtTaskGateway;

    @Autowired
    private SrtSplitProcessor srtSplitProcessor;

    @Autowired
    private SrtCalibrateProcessor srtCalibrateProcessor;

    @Autowired
    private SrtTranslateProcessor srtTranslateProcessor;

    @Autowired
    private SrtCompleteProcessor srtCompleteProcessor;

    @Autowired
    private SrtTaskService srtTaskService;

    /**
     * 运行完整演示
     */
    public void runDemo() {
        log.info("=== 开始SRT字幕翻译任务演示 ===");

        // 1. 创建示例SRT任务
        SrtTask task = createSampleTask();
        log.info("创建SRT任务: {}", task.getTaskId());

        // 2. 保存任务
        task = srtTaskGateway.save(task);
        log.info("任务已保存，状态: {}", task.getStatus());

        // 3. 执行完整流程
        executeFullWorkflow(task);

        // 4. 显示结果
        displayResults(task.getTaskId());

        log.info("=== SRT字幕翻译任务演示完成 ===");
    }

    /**
     * 创建示例任务
     */
    private SrtTask createSampleTask() {
        String sampleSrt = """
            1
            00:00:01,000 --> 00:00:03,000
            Hello World

            2
            00:00:04,000 --> 00:00:06,000
            This is a test subtitle

            3
            00:00:07,000 --> 00:00:09,000
            Welcome to SRT translation system

            4
            00:00:10,000 --> 00:00:12,000
            Artificial intelligence is changing the world

            5
            00:00:13,000 --> 00:00:15,000
            Technology makes life better

            """;

        SrtTask task = new SrtTask();
        task.setTaskId("DEMO_SRT_" + System.currentTimeMillis());
        task.setWorkId("DEMO_WORK_001");

        // 初始化进度信息
        SrtTaskContext.ProgressInfo progressInfo = new SrtTaskContext.ProgressInfo();
        progressInfo.setTotalSubtitles(5);
        progressInfo.setProcessedCount(0);
        progressInfo.setCurrentPhase("初始化 - 目标语言: 中英双语");
        progressInfo.setCompletionPercentage(0.0);

        SrtTaskContext context = new SrtTaskContext();
        context.setSrtFileUrl("https://example.com/demo.srt");
        context.setOriginalContent(sampleSrt);
        context.setProgressInfo(progressInfo);

        task.setContext(context);
        return task;
    }

    /**
     * 执行完整工作流程
     */
    private void executeFullWorkflow(SrtTask task) {
        log.info("开始执行完整工作流程...");

        // 使用服务层处理
        task = srtTaskService.processTaskToCompletion(task);

        // 保存最终状态
        srtTaskGateway.save(task);
    }

    /**
     * 显示结果
     */
    private void displayResults(String taskId) {
        log.info("=== 处理结果 ===");

        srtTaskGateway.findById(taskId).ifPresent(task -> {
            log.info("任务ID: {}", task.getTaskId());
            log.info("工作ID: {}", task.getWorkId());
            log.info("最终状态: {}", task.getStatus());
            log.info("进度: {:.1f}%", task.getProgress() * 100);

            SrtTaskContext context = task.getContext();
            if (context != null) {
                log.info("SRT文件URL: {}", context.getSrtFileUrl());
                // 从进度信息中提取目标语言
                String targetLanguage = context.getProgressInfo() != null && context.getProgressInfo().getCurrentPhase() != null ?
                    context.getProgressInfo().getCurrentPhase().replace("初始化 - 目标语言: ", "") : "未知";
                log.info("目标语言: {}", targetLanguage);

                SrtTaskContext.ProgressInfo progressInfo = context.getProgressInfo();
                if (progressInfo != null) {
                    log.info("总字幕数: {}", progressInfo.getTotalSubtitles());
                    log.info("已处理: {}", progressInfo.getProcessedCount());
                    log.info("当前阶段: {}", progressInfo.getCurrentPhase());
                    log.info("完成度: {:.1f}%", progressInfo.getCompletionPercentage());
                }

                // 显示分块信息
                List<SrtTaskContext.SrtChunk> chunks = context.getSrtChunks();
                if (chunks != null && !chunks.isEmpty()) {
                    log.info("分块数量: {}", chunks.size());
                    for (int i = 0; i < Math.min(3, chunks.size()); i++) {
                        SrtTaskContext.SrtChunk chunk = chunks.get(i);
                        log.info("分块 {}: {}-{} 字幕",
                                i + 1, chunk.getStartSequence(), chunk.getEndSequence());
                    }
                    if (chunks.size() > 3) {
                        log.info("... 还有 {} 个分块", chunks.size() - 3);
                    }
                }

                // 显示错误信息（如果有）
                if (progressInfo != null && progressInfo.getCurrentPhase() != null &&
                    progressInfo.getCurrentPhase().contains("错误")) {
                    log.error("处理状态: {}", progressInfo.getCurrentPhase());
                }
            }
        });
    }

    /**
     * 批量处理演示
     */
    public void runBatchDemo() {
        log.info("=== 批量处理演示 ===");

        // 创建多个任务
        for (int i = 1; i <= 3; i++) {
            SrtTask task = createSampleTask();
            task.setTaskId("BATCH_SRT_" + i + "_" + System.currentTimeMillis());
            task.setWorkId("BATCH_WORK_" + i);

            // 异步处理任务
            new Thread(() -> {
                try {
                    log.info("开始处理任务: {}", task.getTaskId());
                    SrtTask result = srtTaskService.processTaskToCompletion(task);
                    srtTaskGateway.save(result);
                    log.info("任务处理完成: {}, 状态: {}", result.getTaskId(), result.getStatus());
                } catch (Exception e) {
                    log.error("任务处理失败: {}", task.getTaskId(), e);
                }
            }).start();
        }

        log.info("批量任务已提交，正在处理中...");
    }
}