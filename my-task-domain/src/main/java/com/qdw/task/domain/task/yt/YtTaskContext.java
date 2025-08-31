package com.qdw.task.domain.task.yt;

import lombok.Data;

@Data
public class YtTaskContext {

    private String videoUrl;
    private String downloadVideoTaskId;
    private String downloadVideoUrl;
    private String downloadAudioTaskId;
    private String downloadAudioUrl;
    private String cleanedText;
    private String summary;
    private String title;

}
