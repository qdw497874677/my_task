package com.qdw.task.domain.task.yt.gateway;

import lombok.Data;

@Data
public class CheckTaskResponse {

    private String status;
    private CheckTaskResponseData data;
    private String message;

    @Data
    static public class CheckTaskResponseData {
        private String id;
        private String url;
        private String status;
    }

}
