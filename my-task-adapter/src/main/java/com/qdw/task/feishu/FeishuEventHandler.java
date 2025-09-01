package com.qdw.task.feishu;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.lark.oapi.ws.Client;
import com.qdw.task.feishu.command.FeishuMessageProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


@Service
public class FeishuEventHandler {

    @Autowired
    private FeishuMessageProcessor messageProcessor;

    @Value("${feishu.appid}")
    private String appId;

    @Value("${feishu.appsecret}")
    private String appSecret;

    // onP2MessageReceiveV1 为接收消息 v2.0；onCustomizedEvent 内的 message 为接收消息 v1.0。
    private final EventDispatcher EVENT_HANDLER = EventDispatcher.newBuilder("", "")
            .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                @Override
                public void handle(P2MessageReceiveV1 event) {
                    System.out.printf("[ onP2MessageReceiveV1 access ], data: %s\n", Jsons.DEFAULT.toJson(event.getEvent()));
                    System.out.println(JSONObject.toJSONString(event.getEvent().getMessage().getContent()));
                    
                    // 使用消息处理器处理消息
                    messageProcessor.processMessage(event);
                }
            })
            .onCustomizedEvent("这里填入你要自定义订阅的 event 的 key，例如 out_approval", new CustomEventHandler() {
                @Override
                public void handle(EventReq event) {
                    System.out.printf("[ onCustomizedEvent access ], type: message, data: %s\n", new String(event.getBody(), StandardCharsets.UTF_8));
                }
            })
            .build();

    @PostConstruct
    public void init() {
        Client cli = new Client.Builder(appId, appSecret)
                .eventHandler(EVENT_HANDLER)
                .build();
        cli.start();
    }
}
