package com.qdw.task.domain.task.srt.gateway;

import com.qdw.task.domain.task.srt.SrtFlowStatus;
import com.qdw.task.domain.task.srt.SrtTask;
import com.qdw.task.domain.task.srt.SrtTaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SRT任务数据访问网关（Mock实现）
 *
 * 负责SRT任务的持久化和查询操作
 */
@Slf4j
@Service
public class SrtTaskGateway {

    /**
     * 模拟内存存储
     */
    private final Map<String, SrtTask> taskStore = new ConcurrentHashMap<>();

    /**
     * 保存任务
     *
     * @param task 要保存的任务
     * @return 保存后的任务
     */
    public SrtTask save(SrtTask task) {
        if (task.getTaskId() == null) {
            log.warn("任务ID为空，生成新ID");
            task.setTaskId(generateTaskId());
        }

        log.info("保存SRT任务，任务ID: {}, 状态: {}", task.getTaskId(), task.getStatus());
        taskStore.put(task.getTaskId(), task);
        return task;
    }

    /**
     * 根据任务ID查询任务
     *
     * @param taskId 任务ID
     * @return 任务对象
     */
    public Optional<SrtTask> findById(String taskId) {
        log.debug("查询SRT任务，任务ID: {}", taskId);
        return Optional.ofNullable(taskStore.get(taskId));
    }

    /**
     * 根据工作ID查询任务
     *
     * @param workId 工作ID
     * @return 任务对象
     */
    public Optional<SrtTask> findByWorkId(String workId) {
        log.debug("根据工作ID查询SRT任务，工作ID: {}", workId);
        return taskStore.values().stream()
                .filter(task -> workId.equals(task.getWorkId()))
                .findFirst();
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 新状态
     * @return 更新后的任务
     */
    public Optional<SrtTask> updateStatus(String taskId, SrtFlowStatus status) {
        log.info("更新SRT任务状态，任务ID: {}, 新状态: {}", taskId, status);
        return findById(taskId).map(task -> {
            task.setStatus(status);
            return save(task);
        });
    }

    /**
     * 更新任务上下文
     *
     * @param taskId 任务ID
     * @param context 新上下文
     * @return 更新后的任务
     */
    public Optional<SrtTask> updateContext(String taskId, SrtTaskContext context) {
        log.info("更新SRT任务上下文，任务ID: {}", taskId);
        return findById(taskId).map(task -> {
            task.setContext(context);
            return save(task);
        });
    }

    /**
     * 设置错误信息
     *
     * @param taskId 任务ID
     * @param errorMsg 错误信息
     * @return 更新后的任务
     */
    public Optional<SrtTask> setError(String taskId, String errorMsg) {
        log.warn("设置SRT任务错误，任务ID: {}, 错误: {}", taskId, errorMsg);
        return findById(taskId).map(task -> {
            task.setErrorMsg(errorMsg);
            return save(task);
        });
    }

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 是否删除成功
     */
    public boolean deleteById(String taskId) {
        log.info("删除SRT任务，任务ID: {}", taskId);
        return taskStore.remove(taskId) != null;
    }

    /**
     * 获取所有任务
     *
     * @return 任务列表
     */
    public Map<String, SrtTask> findAll() {
        log.debug("查询所有SRT任务，共 {} 个", taskStore.size());
        return new HashMap<>(taskStore);
    }

    /**
     * 根据状态查询任务
     *
     * @param status 任务状态
     * @return 任务列表
     */
    public Map<String, SrtTask> findByStatus(Object status) {
        log.debug("根据状态查询SRT任务，状态: {}", status);
        Map<String, SrtTask> result = new HashMap<>();
        taskStore.forEach((taskId, task) -> {
            if (status.equals(task.getStatus())) {
                result.put(taskId, task);
            }
        });
        return result;
    }

    /**
     * 获取任务统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", taskStore.size());

        // 按状态统计
        Map<Object, Long> statusCount = taskStore.values().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        SrtTask::getStatus,
                        java.util.stream.Collectors.counting()
                ));
        stats.put("statusCount", statusCount);

        // 计算平均进度
        double avgProgress = taskStore.values().stream()
                .mapToDouble(SrtTask::getProgress)
                .average()
                .orElse(0.0);
        stats.put("averageProgress", avgProgress);

        log.info("SRT任务统计: {}", stats);
        return stats;
    }

    /**
     * 清空所有任务（用于测试）
     */
    public void clearAll() {
        log.info("清空所有SRT任务");
        taskStore.clear();
    }

    /**
     * 初始化示例数据（用于测试）
     */
    public void initSampleData() {
        log.info("初始化SRT任务示例数据");

        // 示例SRT内容
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

            """;

        // 创建示例任务
        SrtTask sampleTask = new SrtTask();
        sampleTask.setTaskId("SAMPLE_SRT_001");
        sampleTask.setWorkId("SAMPLE_WORK_001");
        sampleTask.setStatus(com.qdw.task.domain.task.srt.SrtFlowStatus.已创建);

        com.qdw.task.domain.task.srt.SrtTaskContext context = new com.qdw.task.domain.task.srt.SrtTaskContext();
        context.setSrtFileUrl("https://example.com/sample.srt");
        context.setOriginalContent(sampleSrt);
        context.setProgressInfo(new com.qdw.task.domain.task.srt.SrtTaskContext.ProgressInfo());
        context.getProgressInfo().setTotalSubtitles(3);
        context.getProgressInfo().setProcessedCount(0);
        context.getProgressInfo().setCurrentPhase("示例任务");
        context.getProgressInfo().setCompletionPercentage(0.0);

        sampleTask.setContext(context);
        taskStore.put(sampleTask.getTaskId(), sampleTask);

        log.info("示例任务初始化完成");
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "SRT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}