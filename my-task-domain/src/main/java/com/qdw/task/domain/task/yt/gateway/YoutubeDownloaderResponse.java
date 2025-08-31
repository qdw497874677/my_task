package com.qdw.task.domain.task.yt.gateway;

import lombok.Data;

@Data
public class YoutubeDownloaderResponse {

    private String task_id;
    private String status;
    private String message;

}
