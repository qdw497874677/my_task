package com.qdw.task.domain.task.yt;

import com.qdw.task.domain.lock.LocalConcurrency;
import com.qdw.task.domain.lock.Lock;
import com.qdw.task.domain.task.base.BaseTask;
import com.qdw.task.domain.task.base.BaseTaskMapper;
import com.qdw.task.domain.task.base.BaseTaskRouter;
import com.qdw.task.domain.task.base.TaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class YtTaskRouter extends BaseTaskRouter<YtFlowStatus, YtTaskContext, YtTask> {

    @Autowired
    @Qualifier("ytDownloadCreate")
    private TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> ytDownload;

    @Autowired
    @Qualifier("ytDownloadFetch")
    private TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> ytDownloadFetch;

    @Autowired
    @Qualifier("ytDownloadAudioCreate")
    private TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> ytDownloadAudioCreate;

    @Autowired
    @Qualifier("ytDownloadAudioFetch")
    private TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> ytDownloadAudioFetch;



    @Autowired
    private BaseTaskMapper<YtFlowStatus, YtTaskContext, YtTask> baseTaskMapper;
//    private final Step2Cleaner step2;
//    private final Step3Summarizer step3;
    private final LocalConcurrency localConcurrency = new LocalConcurrency(1);

    public void poll() {
        List<YtTask> taskList = baseTaskMapper.getPending();
        for (YtTask ytTask : taskList) {
            this.process(ytTask);
        }
    }

    @Override
    public Lock getLock() {
        return localConcurrency;
    }

    @Override
    public BaseTaskMapper<YtFlowStatus, YtTaskContext, YtTask> getBaseTaskMapper() {
        return baseTaskMapper;
    }

    @Override
    public List<TaskProcessor<YtFlowStatus, YtTaskContext, YtTask>> register() {
        List<TaskProcessor<YtFlowStatus, YtTaskContext, YtTask>> list = new ArrayList<>();
        list.add(ytDownload);
        list.add(ytDownloadFetch);
        list.add(ytDownloadAudioCreate);
        list.add(ytDownloadAudioFetch);
        return list;
    }

//    @Override
//    public ProcessResult<SummaryFlowStatus, SummaryTaskContext> process(SummaryTask task) {
//        return null;
//    }

    @Override
    public void handlerError(BaseTask<YtFlowStatus, YtTaskContext> task, Throwable t) {

    }

    @Override
    public YtFlowStatus getStatus() {
        return null;
    }

    @Override
    public YtFlowStatus nextStatus() {
        return null;
    }
}
