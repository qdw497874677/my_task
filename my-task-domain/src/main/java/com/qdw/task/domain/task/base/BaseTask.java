package com.qdw.task.domain.task.base;

import lombok.Data;

@Data
public abstract class BaseTask<S, C> {
    private String recordId;
    private String taskId;
    private String workId;
    private String errorMsg;
    private S status;
    private C context;
    private String lockExpireTime;

    public abstract S getCurStatus();

    public abstract S getCreated();

    public abstract S getCompleted();

    public abstract S getErrorStatus();


}
