package com.qdw.task.domain.task.srt;

import com.qdw.task.domain.task.base.BaseFlowStatus;

public enum SrtFlowStatus implements BaseFlowStatus {
    已创建,
    已切分,
    已英文校准,
    已翻译,
    已完成,
    异常;

    @Override
    public String getCreated() {
        return SrtFlowStatus.已创建.name();
    }

    @Override
    public String getCompleted() {
        return SrtFlowStatus.已完成.name();
    }

    @Override
    public String getError() {
        return SrtFlowStatus.异常.name();
    }
}