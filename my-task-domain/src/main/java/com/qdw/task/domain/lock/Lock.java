package com.qdw.task.domain.lock;

public interface Lock {

    boolean tryLock(String taskId, long timeoutMs);

    void unlock(String taskId);

}
