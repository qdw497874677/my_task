package com.qdw.task.domain.task.yt.gateway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckTaskRequest {

    private String url;
    private String format;
    private boolean quality;
    private String output_path;

}
