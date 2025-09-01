package com.qdw.task.domain.task.yt.processor;

import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import com.qdw.task.domain.task.yt.YtFlowStatus;
import com.qdw.task.domain.task.yt.YtTask;
import com.qdw.task.domain.task.yt.YtTaskContext;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderGateway;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YtDownloadAudioCreate implements TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> {

//    private final AsrClient asr; // 你自己的语音识别 SDK


    @Autowired
    private YoutubeDownloaderGateway youtubeDownloaderGateway;

    @Override
    public ProcessResult<YtFlowStatus, YtTaskContext> process(YtTask task) {
        YoutubeDownloaderResponse downloaderResponse = youtubeDownloaderGateway.createTask(task.getContext().getVideoUrl(), "bestaudio[ext=m4a]/bestaudio");
//        YoutubeDownloaderResponse downloaderResponse = youtubeDownloaderGateway.createTask(task.getContext().getVideoUrl(), "bestaudio");
        ProcessResult<YtFlowStatus, YtTaskContext> processResult = new ProcessResult<>(nextStatus(), task.getContext());
        task.getContext().setDownloadAudioTaskId(downloaderResponse.getTask_id());
        return processResult;
    }

    @Override
    public YtFlowStatus getStatus() {
        return YtFlowStatus.已完成下载视频;
    }

    @Override
    public YtFlowStatus nextStatus() {
        return YtFlowStatus.已开始下载音频;
    }


}
