package com.qdw.task.domain.ai;

import ai.z.openapi.ZhipuAiClient;
import org.springframework.stereotype.Service;

@Service
public class ZAiServiceDemo {

    ZhipuAiClient client = ZhipuAiClient.builder()
            .apiKey("fb1260a5c8a84a718a99a28627f37f39.zSlckhiameGhNR7M")
            .build();

    public void chat() {

    }

}
