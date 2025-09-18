package com.qdw.task.domain.task.srt;

import com.qdw.task.domain.task.base.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SRT任务服务
 *
 * 负责SRT翻译任务的创建、状态管理和处理流程控制
 */
@Slf4j
@Service
public class SrtTaskService {

    @Autowired
    private List<TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask>> srtProcessors;

    /**
     * 处理器映射
     */
    private final Map<SrtFlowStatus, TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask>> processorMap = new HashMap<>();

    /**
     * 初始化处理器映射
     */
    public void initProcessorMap() {
        if (srtProcessors != null) {
            for (TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> processor : srtProcessors) {
                processorMap.put(processor.getStatus(), processor);
            }
        }
        log.info("SRT处理器映射初始化完成，共 {} 个处理器", processorMap.size());
    }

    /**
     * 创建SRT翻译任务
     *
     * @param srtFileUrl SRT文件URL
     * @param originalContent 原始SRT内容
     * @return 创建的任务
     */
    public SrtTask createTask(String srtFileUrl, String originalContent) {
        log.info("创建SRT翻译任务，文件URL: {}", srtFileUrl);

        SrtTask task = new SrtTask();
        task.setTaskId(generateTaskId());
        task.setWorkId(generateWorkId());

        // 设置初始状态
        task.setStatus(SrtFlowStatus.已创建);

        // 设置任务上下文
        SrtTaskContext context = new SrtTaskContext();
        context.setSrtFileUrl(srtFileUrl);
        context.setOriginalContent(originalContent);
        context.setProgressInfo(new SrtTaskContext.ProgressInfo());
        context.getProgressInfo().setCurrentPhase("任务创建");
        context.getProgressInfo().setCompletionPercentage(0.0);

        task.setContext(context);

        log.info("SRT翻译任务创建成功，任务ID: {}", task.getTaskId());
        return task;
    }

    /**
     * 处理任务
     *
     * @param task 要处理的任务
     * @return 处理结果
     */
    public SrtTask processTask(SrtTask task) {
        log.info("开始处理SRT任务，任务ID: {}, 当前状态: {}", task.getTaskId(), task.getStatus());

        try {
            // 确保处理器映射已初始化
            if (processorMap.isEmpty()) {
                initProcessorMap();
            }

            // 获取当前状态的处理器
            TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> processor = processorMap.get(task.getStatus());
            if (processor == null) {
                log.warn("未找到状态 {} 对应的处理器", task.getStatus());
                task.setStatus(SrtFlowStatus.异常);
                task.setErrorMsg("未找到对应的处理器");
                return task;
            }

            // 执行处理
            var result = processor.process(task);

            // 更新任务状态
            task.setStatus(result.getNextStatus());
            if (result.getUpdateContext() != null) {
                task.setContext(result.getUpdateContext());
            }

            log.info("SRT任务处理完成，任务ID: {}, 新状态: {}", task.getTaskId(), task.getStatus());

            return task;

        } catch (Exception e) {
            log.error("SRT任务处理失败，任务ID: {}", task.getTaskId(), e);
            task.setStatus(SrtFlowStatus.异常);
            task.setErrorMsg(e.getMessage());
            return task;
        }
    }

    /**
     * 批量处理任务直到完成
     *
     * @param task 要处理的任务
     * @return 最终处理结果
     */
    public SrtTask processTaskToCompletion(SrtTask task) {
        log.info("开始批量处理SRT任务至完成，任务ID: {}", task.getTaskId());

        int maxIterations = 10; // 防止无限循环
        int iteration = 0;

        while (!isTaskCompleted(task) && iteration < maxIterations) {
            iteration++;
            log.debug("处理第 {} 轮，当前状态: {}", iteration, task.getStatus());

            task = processTask(task);

            if (task.getStatus() == SrtFlowStatus.异常) {
                log.error("任务处理异常，任务ID: {}, 错误信息: {}", task.getTaskId(), task.getErrorMsg());
                break;
            }

            if (task.getStatus() == SrtFlowStatus.已完成) {
                log.info("任务处理完成，任务ID: {}", task.getTaskId());
                break;
            }

            // 短暂等待，避免过快处理
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (iteration >= maxIterations) {
            log.warn("任务处理达到最大迭代次数，任务ID: {}", task.getTaskId());
            task.setStatus(SrtFlowStatus.异常);
            task.setErrorMsg("处理超时");
        }

        return task;
    }

    /**
     * 检查任务是否已完成
     */
    private boolean isTaskCompleted(SrtTask task) {
        return task.getStatus() == SrtFlowStatus.已完成 || task.getStatus() == SrtFlowStatus.异常;
    }

    /**
     * 获取任务进度信息
     */
    public SrtTaskContext.ProgressInfo getTaskProgress(SrtTask task) {
        if (task.getContext() == null) {
            return null;
        }
        return task.getContext().getProgressInfo();
    }

    /**
     * 获取任务状态描述
     */
    public String getTaskStatusDescription(SrtTask task) {
        if (task == null) {
            return "任务不存在";
        }

        switch (task.getStatus()) {
            case 已创建:
                return "任务已创建，等待处理";
            case 已切分:
                return "字幕文件已切分完成";
            case 已英文校准:
                return "英文内容已校准完成";
            case 已翻译:
                return "字幕翻译已完成";
            case 已完成:
                return "任务已完成";
            case 异常:
                return "任务异常: " + (task.getErrorMsg() != null ? task.getErrorMsg() : "未知错误");
            default:
                return "未知状态: " + task.getStatus();
        }
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "SRT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 生成工作ID
     */
    private String generateWorkId() {
        return "WORK_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 获取处理器映射信息（用于调试）
     */
    public Map<String, String> getProcessorInfo() {
        Map<String, String> info = new HashMap<>();
        processorMap.forEach((status, processor) -> {
            info.put(status.name(), processor.getClass().getSimpleName());
        });
        return info;
    }
}