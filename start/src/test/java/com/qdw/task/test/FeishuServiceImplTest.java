package com.qdw.task.test;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.bitable.v1.model.ListAppTableResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.im.v1.model.CreateMessageRespBody;
import com.qdw.task.api.feishu.IFeishuBitTableService;
import com.qdw.task.api.feishu.IFeishuService;
import com.qdw.task.domain.task.summary.SummaryTaskRouter;
import com.qdw.task.domain.task.yt.YtTaskRouter;
import com.qdw.task.domain.task.yt.gateway.CheckTaskResponse;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderGateway;
import com.qdw.task.domain.task.yt.gateway.YoutubeDownloaderResponse;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeishuServiceImplTest {

    @Autowired
    private IFeishuService feishuService;

    @Autowired
    private IFeishuBitTableService feishuBitTableService;


    @Autowired
    private SummaryTaskRouter summaryTaskRouter;

    @Autowired
    private YtTaskRouter ytTaskRouter;

    @Autowired
    private YoutubeDownloaderGateway youtubeDownloaderGateway;

    @Before
    public void setUp() {

    }

    @Test
    void getClient() {
        CreateMessageRespBody test = feishuService.sendMsg("test");
        System.out.println(JSONObject.toJSONString(test));
    }

    @Test
    void bitableListTest() {
        ListAppTableResp listAppTableResp = feishuBitTableService.bitableList("MJiQbSYWzae7hKsGK79cHvUVnwK");
        System.out.println(JSONObject.toJSONString(listAppTableResp));
    }

    @Test
    void searchAppTableRecord() {
        SearchAppTableRecordResp searchAppTableRecordResp = feishuBitTableService.searchAppTableRecord(
                "MJiQbSYWzae7hKsGK79cHvUVnwK",
                "tblzaDCN5tXt04mM",
                SearchAppTableRecordReqBody.newBuilder().build()
        );
        System.out.println(JSONObject.toJSONString(searchAppTableRecordResp));
    }

    @Test
    void taskFlowScheduler() {
        summaryTaskRouter.poll();
    }

    @Test
    void testYt() {
        ytTaskRouter.poll();
    }

    @Test
    void youtubeDownloaderGateway() {
        YoutubeDownloaderResponse task = youtubeDownloaderGateway.createTask("https://www.youtube.com/watch?v=C5Wor9vxsNA", "bestaudio[ext=m4a]");
        System.out.println(JSONObject.toJSONString(task));
    }

    @Test
    void youtubeDownloaderCheckGateway() {
        CheckTaskResponse checkTaskResponse = youtubeDownloaderGateway.checkTask("87de7273-0943-46a6-80d4-8fdc8af59c5d");
        System.out.println(JSONObject.toJSONString(checkTaskResponse));
    }



}
