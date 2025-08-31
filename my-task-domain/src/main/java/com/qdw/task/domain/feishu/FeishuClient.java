package com.qdw.task.domain.feishu;


import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;

import java.util.concurrent.TimeUnit;


public class FeishuClient {

    private Client client = Client.newBuilder("appId","appSecret") // 默认配置为自建应用
            .marketplaceApp() // 设置应用类型为商店应用
            .openBaseUrl(BaseUrlEnum.FeiShu) // 设置域名，默认为飞书
            .helpDeskCredential("helpDeskId","helpDeskSecret") // 服务台应用才需要设置
            .requestTimeout(3, TimeUnit.SECONDS) // 设置httpclient 超时时间，默认永不超时
            .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers、body 等信息。
            .build();

    public Client getClient() {
        return client;
    }

}
