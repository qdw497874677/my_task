package com.qdw.task.domain.task.base;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessResult<S, T> {
    private S nextStatus;
    private T updateContext;
    private Map<String, Object> updateFields;

    public ProcessResult(S nextStatus, T updateContext) {
        this.nextStatus = nextStatus;
        this.updateContext = updateContext;
    }

    public ProcessResult() {

    }
}
