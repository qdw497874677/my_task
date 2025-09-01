package com.qdw.task.domain.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LocalConcurrency implements Lock {
    // 1. 每条任务一把锁
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    // 2. 全局并发控制（可配置）
    private final Semaphore semaphore;

    public LocalConcurrency(int permits) {
        this.semaphore = new Semaphore(permits);
    }

    /**
     * 获取任务级锁 + 全局令牌，两步都成功才返回 true
     */
    @Override
    public boolean tryLock(String taskId, long timeoutMs) {
        try {
            if (!semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                return false;          // 全局并发已满
            }
            ReentrantLock lock = locks.computeIfAbsent(taskId, k -> new ReentrantLock());
            if (!lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                semaphore.release();   // 全局令牌还回去
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void unlock(String taskId) {
        ReentrantLock lock = locks.get(taskId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
        semaphore.release();
    }
}
