package com.qdw.task.task;

import com.qdw.task.api.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务示例
 * 每5秒执行一次
 */
//@Component
@Slf4j
public class ScheduledTask {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TaskService taskService;

    /**
     * 每5秒执行一次的定时任务
     * 使用 fixedRate 表示上一次开始执行时间点之后5秒再执行
     */
    @Scheduled(fixedRate = 5000)
    public void executeEveryFiveSeconds() {
        String currentTime = LocalDateTime.now().format(formatter);
        log.info("定时任务执行中... 当前时间: {}", currentTime);
        
        // 在这里添加你的业务逻辑
        performTask();
    }

    /**
     * 执行具体的任务逻辑
     */
    private void performTask() {
        try {
            // 示例：模拟一些业务处理
            log.info("执行业务逻辑...");

            taskService.pushPendingOnce();
            
            log.info("业务逻辑执行完成");
            
        } catch (Exception e) {
            log.error("定时任务执行发生异常", e);
        }
    }

    /**
     * 另一种定时任务配置方式：使用 fixedDelay
     * 上一次执行完毕时间点之后5秒再执行
     */
    @Scheduled(fixedDelay = 5000)
    public void executeWithFixedDelay() {
        String currentTime = LocalDateTime.now().format(formatter);
        log.info("fixedDelay 定时任务执行... 当前时间: {}", currentTime);
    }

    /**
     * 使用 cron 表达式的定时任务示例
     * 每5秒执行一次：
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void executeWithCron() {
        String currentTime = LocalDateTime.now().format(formatter);
        log.info("cron 定时任务执行... 当前时间: {}", currentTime);
    }
}
