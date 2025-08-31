package com.qdw.task.domain.task.base;

import java.util.List;

public interface BaseTaskMapper<S, C, T> {

    List<T> getPending();

    boolean casLock(T task);

    void finish(T task, ProcessResult<S, C> result);

}
