package com.qdw.task.api.feishu;

import com.lark.oapi.Client;
import com.lark.oapi.service.bitable.v1.model.*;
import com.lark.oapi.service.docx.v1.model.ConvertDocumentResp;
import com.lark.oapi.service.docx.v1.model.CreateDocumentResp;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import com.lark.oapi.service.im.v1.model.CreateMessageRespBody;
import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.im.v1.model.ReplyMessageResp;

import java.util.Map;

public interface IFeishuService {

    CreateMessageRespBody sendMsg(String msg);
    
    CreateMessageRespBody sendMsg(String msg, String receiveId, String receiveIdType);

    ReplyMessageResp replyMessage(String msg, String messageId, String receiveIdType);

    ConvertDocumentResp convertDocument(String folderToken);
    
    // 为FeishuTaskProgressService提供访问Client的途径
    Client getClient();

    CreateDocumentResp createDocument(String folderToken, String title);



}
