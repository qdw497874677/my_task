package com.qdw.task.domain.task.yt.processor;

import com.alibaba.fastjson.JSONObject;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import com.qdw.task.domain.task.yt.YtFlowStatus;
import com.qdw.task.domain.task.yt.YtTask;
import com.qdw.task.domain.task.yt.YtTaskContext;
import com.qdw.task.domain.task.yt.gateway.CheckTaskResponse;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YtDownloadAudioFetch implements TaskProcessor<YtFlowStatus, YtTaskContext, YtTask> {

//    private final AsrClient asr; // 你自己的语音识别 SDK


    @Autowired
    private YoutubeDownloaderGateway youtubeDownloaderGateway;

    @Override
    public ProcessResult<YtFlowStatus, YtTaskContext> process(YtTask task) {
        String downloadVideoTaskId = task.getContext().getDownloadVideoTaskId();
        CheckTaskResponse checkTaskResponse = youtubeDownloaderGateway.checkTask(downloadVideoTaskId);
        if ("completed".equals(checkTaskResponse.getData().getStatus())) {
            String downloadUrl = youtubeDownloaderGateway.getDownloadUrl(downloadVideoTaskId);
            task.getContext().setDownloadAudioUrl(downloadUrl);
            return new ProcessResult<>(nextStatus(), task.getContext());
        } else {
            task.getContext().setDownloadVideoUrl("还未完成，当前结果为：" + JSONObject.toJSONString(checkTaskResponse));
            return new ProcessResult<>(getStatus(), task.getContext());
        }

    }

    @Override
    public YtFlowStatus getStatus() {
        return YtFlowStatus.已开始下载音频;
    }

    @Override
    public YtFlowStatus nextStatus() {
        return YtFlowStatus.已完成下载音频;
    }


}
