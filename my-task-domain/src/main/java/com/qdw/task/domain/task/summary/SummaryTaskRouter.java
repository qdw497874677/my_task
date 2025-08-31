package com.qdw.task.domain.task.summary;

import com.qdw.task.domain.task.base.BaseTask;
import com.qdw.task.domain.task.base.BaseTaskMapper;
import com.qdw.task.domain.task.base.BaseTaskRouter;
import com.qdw.task.domain.task.base.TaskProcessor;
import com.qdw.task.domain.lock.LocalConcurrency;
import com.qdw.task.domain.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SummaryTaskRouter extends BaseTaskRouter<SummaryFlowStatus, SummaryTaskContext, SummaryTask> {

    @Autowired
    private Step1Transcriber step1;
    @Autowired
    private BaseTaskMapper<SummaryFlowStatus, SummaryTaskContext, SummaryTask> baseTaskMapper;
//    private final Step2Cleaner step2;
//    private final Step3Summarizer step3;
    private final LocalConcurrency localConcurrency = new LocalConcurrency(1);

    public void poll() {
        List<SummaryTask> taskList = baseTaskMapper.getPending();
        for (SummaryTask summaryTask : taskList) {
            this.process(summaryTask);
        }
    }

    @Override
    public Lock getLock() {
        return localConcurrency;
    }

    @Override
    public BaseTaskMapper<SummaryFlowStatus, SummaryTaskContext, SummaryTask> getBaseTaskMapper() {
        return baseTaskMapper;
    }

    @Override
    public List<TaskProcessor<SummaryFlowStatus, SummaryTaskContext, SummaryTask>> register() {
        List<TaskProcessor<SummaryFlowStatus, SummaryTaskContext, SummaryTask>> list = new ArrayList<>();
        list.add(new Step1Transcriber());
        return list;
    }

//    @Override
//    public ProcessResult<SummaryFlowStatus, SummaryTaskContext> process(SummaryTask task) {
//        return null;
//    }

    @Override
    public void handlerError(BaseTask<SummaryFlowStatus, SummaryTaskContext> task, Throwable t) {

    }

    @Override
    public SummaryFlowStatus getStatus() {
        return SummaryFlowStatus.已创建;
    }

    @Override
    public SummaryFlowStatus nextStatus() {
        return null;
    }
}
