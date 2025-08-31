package com.qdw.task.domain.task.yt;


import com.qdw.task.domain.task.base.BaseTask;

/**
 * @author quandawei
 */
public class YtTask extends BaseTask<YtFlowStatus, YtTaskContext> {


    @Override
    public YtFlowStatus getCurStatus() {
        return null;
    }

    @Override
    public YtFlowStatus getCreated() {
        return YtFlowStatus.已创建;
    }

    @Override
    public YtFlowStatus getCompleted() {
        return YtFlowStatus.已完成;
    }

    @Override
    public YtFlowStatus getErrorStatus() {
        return YtFlowStatus.异常;
    }
}
