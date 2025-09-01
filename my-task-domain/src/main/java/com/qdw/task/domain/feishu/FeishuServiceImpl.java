package com.qdw.task.domain.feishu;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.ICache;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.Condition;
import com.lark.oapi.service.bitable.v1.model.FilterInfo;
import com.lark.oapi.service.bitable.v1.model.GetAppReq;
import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.lark.oapi.service.bitable.v1.model.GetAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.GetAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableReq;
import com.lark.oapi.service.bitable.v1.model.ListAppTableResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.UpdateAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.UpdateAppTableRecordResp;
import com.lark.oapi.service.docx.v1.model.ConvertDocumentReq;
import com.lark.oapi.service.docx.v1.model.ConvertDocumentReqBody;
import com.lark.oapi.service.docx.v1.model.ConvertDocumentResp;
import com.lark.oapi.service.docx.v1.model.CreateDocumentReq;
import com.lark.oapi.service.docx.v1.model.CreateDocumentReqBody;
import com.lark.oapi.service.docx.v1.model.CreateDocumentResp;
import com.lark.oapi.service.drive.v1.model.FileUploadInfo;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileReq;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.CreateMessageRespBody;
import com.lark.oapi.service.im.v1.model.ReplyMessageReq;
import com.lark.oapi.service.im.v1.model.ReplyMessageReqBody;
import com.lark.oapi.service.im.v1.model.ReplyMessageResp;
import com.qdw.task.api.feishu.IFeishuService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FeishuServiceImpl implements IFeishuService {

    @Value("${feishu.appid}")
    private String appId;

    @Value("${feishu.appsecret}")
    private String appSecret;

    private Client client;

    @PostConstruct
    public void init() {


        client = Client.newBuilder(appId, appSecret) // 默认配置为自建应用
//                .marketplaceApp() // 设置应用类型为商店应用
                .openBaseUrl(BaseUrlEnum.FeiShu) // 设置域名，默认为飞书
                .tokenCache(new ICache() {
                    @Override
                    public String get(String s) {
                        return null;
                    }

                    @Override
                    public void set(String s, String s1, int i, TimeUnit timeUnit) {

                    }
                })
//            .helpDeskCredential("helpDeskId","helpDeskSecret") // 服务台应用才需要设置
                .requestTimeout(3, TimeUnit.SECONDS) // 设置httpclient 超时时间，默认永不超时
                .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers、body 等信息。
                .build();
    }

    // 为单元测试提供访问client的途径
    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public CreateMessageRespBody sendMsg(String msg) {
        // 默认发送给固定用户
        return sendMsg(msg, "2ed1a7aa", "user_id");
    }
    
    @Override
    public CreateMessageRespBody sendMsg(String msg, String receiveId, String receiveIdType) {
        // 创建请求对象

        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType)
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType("text")
                        .content(JSONObject.toJSONString(Map.of("text", msg)))
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        // 发起请求
        CreateMessageResp resp = null;
        try {
            resp = client.im().v1().message().create(req);
        } catch (Exception e) {
            System.out.println("sendMsg error:" + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp.getRawResponse().getBody())));
            return null;
        }

        // 业务数据处理
        System.out.println(JSONObject.toJSONString(resp.getData()));
        return resp.getData();
    }

    @Override
    public ReplyMessageResp replyMessage(String msg, String messageId, String receiveIdType) {
        // 创建请求对象
        ReplyMessageReq req = ReplyMessageReq.newBuilder().messageId(messageId)
                .replyMessageReqBody(ReplyMessageReqBody.newBuilder()
                        .content(JSONObject.toJSONString(Map.of("text", msg)))
                        .msgType("text")
                        // 话题模式
                        .replyInThread(false)
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        // 发起请求

        ReplyMessageResp resp = null;
        try {
            resp = client.im().v1().message().reply(req);
        } catch (Exception e) {
            System.out.println("replyMessage error:" + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp)));
            return null;
        }
        return resp;
    }


    @Override
    public ConvertDocumentResp convertDocument(String folderToken) {
        // 创建请求对象
        ConvertDocumentReq req = ConvertDocumentReq.newBuilder()
                .userIdType("open_id")
                .convertDocumentReqBody(ConvertDocumentReqBody.newBuilder()
                        .contentType("markdown")
                        .content("Text **Bold** ")
                .build())
						.build();

        // 发起请求
        ConvertDocumentResp resp = null;
        try {
            resp = client.docx().v1().document().convert(req, RequestOptions.newBuilder()
                    .userAccessToken("u-d9xuuFdJN2Yab7lDsUXw76lg2BD5h1ErPG200gk022y6")
                    .build());
        } catch (Exception e) {
            log.error("convertDocument error", e);
            return null;
        }

        // 处理服务端错误
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp)));
            return null;
        }
        return resp;
    }

    @Override
    public CreateDocumentResp createDocument(String folderToken, String title) {
        // 创建请求对象
        CreateDocumentReq req = CreateDocumentReq.newBuilder()
                .createDocumentReqBody(CreateDocumentReqBody.newBuilder()
                        .folderToken(folderToken)
                        .title(title)
                        .build())
                .build();

        // 发起请求
        CreateDocumentResp resp = null;
        try {
            resp = client.docx().v1().document().create(req, RequestOptions.newBuilder()
                    .build());
        } catch (Exception e) {
            log.error("createDocument error", e);
            return null;
        }


        // 处理服务端错误
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp)));
            return null;
        }
        return resp;
    }

}
