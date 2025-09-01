package com.qdw.task.domain.task.base;

public interface TaskProcessor<S, C, T extends BaseTask<S, C>> {

    ProcessResult<S, C> process(T task);

    S getStatus();

    S nextStatus();

}
