package com.qdw.task.feishu.command;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 飞书命令异步执行器
 * 处理命令的超时和异步执行逻辑
 */
@Slf4j
@Component
public class FeishuCommandExecutor {

    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;

    private final Executor commandExecutor;

    public FeishuCommandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("feishu-command-");
        executor.initialize();
        this.commandExecutor = executor;
    }

    /**
     * 异步执行命令，支持超时控制
     *
     * @param commandName 命令名称
     * @param event 飞书消息事件
     * @param timeoutSeconds 超时时间（秒）
     * @return CompletableFuture 包含执行结果
     */
    public CompletableFuture<String> executeCommandAsync(String commandName, P2MessageReceiveV1 event, int timeoutSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            FeishuTaskCommand command = commandRegistry.getCommand(commandName);
            if (command == null) {
                return "未知命令: " + commandName + "\n" + commandRegistry.generateHelpMessage();
            }

            try {
                return command.execute(event);
            } catch (Exception e) {
                log.error("Error executing command: {}", commandName, e);
                return "执行命令时发生错误: " + e.getMessage();
            }
        }, commandExecutor)
        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .exceptionally(throwable -> {
            if (throwable instanceof TimeoutException) {
                log.warn("Command execution timeout: {}", commandName);
                return "命令执行超时，请稍后重试";
            }
            return "命令执行失败: " + throwable.getMessage();
        });
    }

    /**
     * 检查命令是否启用即时响应
     *
     * @param commandName 命令名称
     * @return 是否启用即时响应
     */
    public boolean isInstantResponseEnabled(String commandName) {
        FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        return command != null && command.isInstantResponseEnabled();
    }

    /**
     * 获取命令的即时响应消息
     *
     * @param commandName 命令名称
     * @return 即时响应消息
     */
    public String getInstantResponseMessage(String commandName) {
        FeishuTaskCommand command = commandRegistry.getCommand(commandName);
        if (command != null && command.isInstantResponseEnabled()) {
            return command.getInstantResponseMessage();
        }
        return "正在处理您的请求，请稍候...";
    }

    /**
     * 获取命令执行器
     *
     * @return 执行器实例
     */
    public Executor getExecutor() {
        return commandExecutor;
    }
}