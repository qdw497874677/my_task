package com.qdw.task.domain.task.summary;

import com.qdw.task.domain.task.base.BaseTask;

/**
 * @author quandawei
 */
public class SummaryTask extends BaseTask<SummaryFlowStatus, SummaryTaskContext> {


    @Override
    public SummaryFlowStatus getCurStatus() {
        return null;
    }

    @Override
    public SummaryFlowStatus getCreated() {
        return SummaryFlowStatus.已创建;
    }

    @Override
    public SummaryFlowStatus getCompleted() {
        return SummaryFlowStatus.已完成;
    }

    @Override
    public SummaryFlowStatus getErrorStatus() {
        return SummaryFlowStatus.异常;
    }
}
