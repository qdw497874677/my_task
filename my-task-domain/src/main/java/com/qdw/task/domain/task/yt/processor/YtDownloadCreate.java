package com.qdw.task.domain.task.yt.processor;

import com.qdw.task.domain.task.yt.YtFlowStatus;
import com.qdw.task.domain.task.yt.YtTask;
import com.qdw.task.domain.task.yt.YtTaskContext;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderGateway;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YtDownloadCreate implements TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> {

//    private final AsrClient asr; // 你自己的语音识别 SDK


    @Autowired
    private YoutubeDownloaderGateway youtubeDownloaderGateway;

    @Override
    public ProcessResult<YtFlowStatus, YtTaskContext> process(YtTask task) {
        YoutubeDownloaderResponse downloaderResponse = youtubeDownloaderGateway.createTask(task.getContext().getVideoUrl(), "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
        ProcessResult<YtFlowStatus, YtTaskContext> processResult = new ProcessResult<>(nextStatus(), task.getContext());
        task.getContext().setDownloadVideoTaskId(downloaderResponse.getTask_id());
        return processResult;
    }

    @Override
    public YtFlowStatus getStatus() {
        return YtFlowStatus.已创建;
    }

    @Override
    public YtFlowStatus nextStatus() {
        return YtFlowStatus.已开始下载视频;
    }


}
