package com.qdw.task.domain.task.yt.gateway;

public interface YoutubeDownloaderGateway {

    YoutubeDownloaderResponse createTask(String url, String format);

    CheckTaskResponse checkTask(String taskId);

    String getDownloadUrl(String taskId);

}
