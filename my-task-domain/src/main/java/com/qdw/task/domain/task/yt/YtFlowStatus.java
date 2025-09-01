package com.qdw.task.domain.task.yt;

import com.qdw.task.domain.task.base.BaseFlowStatus;

public enum YtFlowStatus implements BaseFlowStatus {
    已创建,
    已开始下载视频,
    已完成下载视频,
    已开始下载音频,
    已完成下载音频,
//    已音频转文字,
    已完成,
    异常;

    @Override
    public String getCreated() {
        return YtFlowStatus.已创建.name();
    }

    @Override
    public String getCompleted() {
        return YtFlowStatus.已完成.name();
    }

    @Override
    public String getError() {
        return YtFlowStatus.异常.name();
    }
}