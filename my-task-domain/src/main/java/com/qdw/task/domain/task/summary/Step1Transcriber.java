package com.qdw.task.domain.task.summary;

import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import org.springframework.stereotype.Component;

@Component
public class Step1Transcriber implements TaskProcessor<SummaryFlowStatus, SummaryTaskContext, SummaryTask> {

//    private final AsrClient asr; // 你自己的语音识别 SDK


    @Override
    public ProcessResult<SummaryFlowStatus, SummaryTaskContext> process(SummaryTask task) {
        ProcessResult<SummaryFlowStatus, SummaryTaskContext> processResult = new ProcessResult<>(task.getCompleted(), task.getContext());
        task.getContext().setCleanedText("test");
        return processResult;
    }

    @Override
    public SummaryFlowStatus getStatus() {
        return SummaryFlowStatus.已创建;
    }

    @Override
    public SummaryFlowStatus nextStatus() {
        return SummaryFlowStatus.已完成;
    }


}
