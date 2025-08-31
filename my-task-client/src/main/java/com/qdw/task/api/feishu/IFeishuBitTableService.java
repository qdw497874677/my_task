package com.qdw.task.api.feishu;

import com.lark.oapi.service.bitable.v1.model.GetAppResp;
import com.lark.oapi.service.bitable.v1.model.GetAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.ListAppTableResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.UpdateAppTableRecordResp;

import java.util.Map;

public interface IFeishuBitTableService {

    GetAppResp bitableGet(String addToken);

    ListAppTableResp bitableList(String appToken);

    SearchAppTableRecordResp searchAppTableRecord(
            String appToken,
            String tableId,
            SearchAppTableRecordReqBody searchAppTableRecordReqBody
    );

    GetAppTableRecordResp getAppTableRecord(String appToken, String tableId, String recordId) ;

    UpdateAppTableRecordResp updateAppTableRecord(
            String appToken, String tableId, String recordId, Map<String, Object> map
    );

    SearchAppTableRecordResp queryPending(
            String appToken,
            String tableId,
            String statusFieldName,
            String[] pendingFieldNames
    );

}
