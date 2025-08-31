package com.qdw.task.domain.task.summary;

import com.qdw.task.domain.task.base.BaseFlowStatus;

public enum SummaryFlowStatus implements BaseFlowStatus {
    已创建,
    已识别,
    已总结,
    处理中,
    已完成,
    异常;

    @Override
    public String getCreated() {
        return SummaryFlowStatus.已创建.name();
    }

    @Override
    public String getCompleted() {
        return SummaryFlowStatus.已完成.name();
    }

    @Override
    public String getError() {
        return SummaryFlowStatus.异常.name();
    }
}