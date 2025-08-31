package com.qdw.task.domain.feishu.bitable;

import com.alibaba.fastjson.JSONObject;
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
import com.qdw.task.api.feishu.IFeishuBitTableService;
import com.qdw.task.api.feishu.IFeishuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IFeishuBitTableServiceImpl implements IFeishuBitTableService {


    @Autowired
    private IFeishuService iFeishuService;


    @Override
    public GetAppResp bitableGet(String addToken) {
        // 创建请求对象
        // 发起请求
        GetAppResp getAppResp = null;
        try {
            getAppResp = iFeishuService.getClient().bitable().v1().app().get(
                    GetAppReq.newBuilder()
//                            .appToken("MJiQbSYWzae7hKsGK79cHvUVnwK")
                            .appToken(addToken)
                            .build());
        } catch (Exception e) {
            System.out.println("bitableGet error:" + e.getMessage());
            return null;
        }
        // 处理服务端错误
        if(!getAppResp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    getAppResp.getCode(), getAppResp.getMsg(), getAppResp.getRequestId(),
                    JSONObject.toJSONString(getAppResp)));
            return getAppResp;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(getAppResp.getData()));
        return null;
    }

    @Override
    public ListAppTableResp bitableList(String appToken) {
        // 创建请求对象
        ListAppTableReq req = ListAppTableReq.newBuilder()
                .pageSize(20)
                .build();

        // 发起请求
        ListAppTableResp resp = null;
        try {
            resp = iFeishuService.getClient().bitable().v1().appTable().list(req, RequestOptions.newBuilder()
//                    .userAccessToken("u-d9xuuFdJN2Yab7lDsUXw76lg2BD5h1ErPG200gk022y6")
                    .userAccessToken(appToken)
                    .build());
        } catch (Exception e) {
            System.out.println("bitableList error:" + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if (!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp)));
            return resp;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        return resp;
    }

    @Override
    public SearchAppTableRecordResp searchAppTableRecord(
            String appToken,
            String tableId,
            SearchAppTableRecordReqBody searchAppTableRecordReqBody
    ) {
        if (searchAppTableRecordReqBody == null) {
            searchAppTableRecordReqBody = SearchAppTableRecordReqBody.newBuilder().build();
        }

        // 创建请求对象
        SearchAppTableRecordReq req = SearchAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .pageSize(20)
                .searchAppTableRecordReqBody(searchAppTableRecordReqBody)
//                .searchAppTableRecordReqBody(SearchAppTableRecordReqBody.newBuilder()
//                        .viewId("vewqhz51lk")
//                        .fieldNames(new String[] {
//                                "字段1",
//                                "字段2"
//                        })
//                        .sort(new Sort[] {
//                                Sort.newBuilder()
//                                        .fieldName("多行文本")
//                                        .desc(true)
//                                        .build()
//                        })
//                        .filter(FilterInfo.newBuilder()
//                                .conjunction("and")
//                                .conditions(new Condition[] {
//                                        Condition.newBuilder()
//                                                .fieldName("职位")
//                                                .operator("is")
//                                                .value(new String[] {
//                                                        "初级销售员"
//                                                })
//                                                .build(),
//                                        Condition.newBuilder()
//                                                .fieldName("销售额")
//                                                .operator("isGreater")
//                                                .value(new String[] {
//                                                        "10000.0"
//                                                })
//                                                .build()
//                                })
//                                .build())
//                        .automaticFields(false)
//                        .build())
                .build();

        // 发起请求
        SearchAppTableRecordResp resp = null;
        try {
            resp = iFeishuService.getClient().bitable().v1().appTableRecord().search(req, RequestOptions.newBuilder()
//                    .userAccessToken("u-d9xuuFdJN2Yab7lDsUXw76lg2BD5h1ErPG200gk022y6")
                    .build());
        } catch (Exception e) {
            System.out.println("searchAppTableRecord error:" + e.getMessage());
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
    public GetAppTableRecordResp getAppTableRecord(String appToken, String tableId, String recordId) {

        // 发起请求
        GetAppTableRecordResp getAppTableRecordResp = null;
        try {
            GetAppTableRecordReq recordReq = GetAppTableRecordReq.newBuilder().appToken(appToken).tableId(tableId).recordId(recordId).build();
            getAppTableRecordResp = iFeishuService.getClient().bitable().v1().appTableRecord().get(recordReq);
        } catch (Exception e) {
            System.out.println("getAppTableRecord error:" + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if(!getAppTableRecordResp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    getAppTableRecordResp.getCode(), getAppTableRecordResp.getMsg(), getAppTableRecordResp.getRequestId(), JSONObject.toJSONString(getAppTableRecordResp)));
            return null;
        }
        return getAppTableRecordResp;
    }

    @Override
    public UpdateAppTableRecordResp updateAppTableRecord(
            String appToken, String tableId, String recordId, Map<String, Object> map) {
        // 创建请求对象
        UpdateAppTableRecordReq req = UpdateAppTableRecordReq.newBuilder()
                .appToken(appToken)
                .tableId(tableId)
                .recordId(recordId)
                .appTableRecord(AppTableRecord.newBuilder()
                        .fields(
                                map
//                                new HashMap<String, Object>() {
//                            {
//                                put("文本", "文本内容");
//                                put("多选", [] Object {});
//                                put("复选框", true);
//                                put("单向关联", [] Object {});
//                                put("条码", "qawqe");
//                                put("单选", "选项3");
//                                put("货币", 3);
//                                put("日期", 1674206443000);
//                                put("双向关联", [] Object {});
//                                put("评分", 3);
//                                put("进度", 0.25);
//                                put("人员", [] Object {});
//                                put("群组", [] Object {});
//                                put("超链接", new HashMap <String, Object > () {
//                                    {
//                                        put("text", "飞书多维表格官网");
//                                        put("link", "https://www.feishu.cn/product/base");
//                                    }
//                                });
//                                put("附件", [] Object {});
//                                put("地理位置", "116.397755,39.903179");
//                                put("索引", "索引列文本类型");
//                                put("数字", 100);
//                                put("电话号码", "13026162666");
//                            }
//                        }
                        )
                        .build())
                .build();

        // 发起请求
        UpdateAppTableRecordResp resp = null;
        try {
            resp = iFeishuService.getClient().bitable().v1().appTableRecord().update(req);
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return null;
        }


        // 处理服务端错误
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), JSONObject.toJSONString(resp)));
            return null;
        }

        return resp;
        // 业务数据处理
    }


    @Override
    public SearchAppTableRecordResp queryPending(String appToken, String tableId, String statusFieldName, String[] pendingfieldNames) {
        SearchAppTableRecordReqBody searchAppTableRecordReqBody = SearchAppTableRecordReqBody.newBuilder()
                .filter(
                        FilterInfo.newBuilder().conjunction("and").conditions(
                                new Condition[]{
                                        Condition.newBuilder()
                                                .fieldName(statusFieldName)
                                                .operator("contains")
                                                .value(pendingfieldNames)
                                                .build(),
                                        Condition.newBuilder()
                                                .fieldName("workId")
                                                .operator("isEmpty")
                                                .value(new String[]{})
                                                .build()
                                }
                        ).build()
                )
                .build();
        return this.searchAppTableRecord(appToken, tableId,
                searchAppTableRecordReqBody
        );
    }




}
