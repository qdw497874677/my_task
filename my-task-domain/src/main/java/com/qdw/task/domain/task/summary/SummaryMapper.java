package com.qdw.task.domain.task.summary;

import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.GetAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordRespBody;
import com.qdw.task.api.feishu.IFeishuBitTableService;
import com.qdw.task.api.feishu.IFeishuService;
import com.qdw.task.domain.task.base.BaseTaskMapper;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.common.StringUtils;
import com.qdw.task.domain.task.base.TaskFileRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SummaryMapper implements BaseTaskMapper<SummaryFlowStatus, SummaryTaskContext, SummaryTask> {

    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private IFeishuBitTableService bitTableService;

    private String appToken = "MJiQbSYWzae7hKsGK79cHvUVnwK";
    private String tableId = "tblzaDCN5tXt04mM";

    private SummaryTask task = new SummaryTask();

//    public SummaryMapper(SummaryTask task) {
//        this.task = task;
//    }

    @Override
    public List<SummaryTask> getPending() {
        List<SummaryTask> taskList = new ArrayList<>();
        SummaryFlowStatus completed = task.getCompleted();
        List<String> statusList = new ArrayList<>();
        for (SummaryFlowStatus value : SummaryFlowStatus.values()) {
            if (!completed.equals(value)) {
                statusList.add(value.name());
            }
        }

        SearchAppTableRecordResp searchAppTableRecordResp = bitTableService.queryPending(appToken, tableId, "flowStatus", statusList.toArray(new String[0]));
        SearchAppTableRecordRespBody data = searchAppTableRecordResp.getData();
        AppTableRecord[] items = data.getItems();
        for (AppTableRecord item : items) {
            SummaryTask task = new SummaryTask();
            task.setRecordId(item.getRecordId());

            SummaryTaskContext context = new SummaryTaskContext();
            task.setContext(context);
            Object audio = item.getFields().get("原始音频数据");
            if (audio != null) {
                List<Object> audioList = (List) audio;
                if (CollectionUtils.isEmpty(audioList)) {
                    context.setAudio(JSONObject.parseObject(JSONObject.toJSONString(audioList.get(0)), TaskFileRow.class));
                }
            }

            task.setStatus(SummaryFlowStatus.valueOf(item.getFields().get("flowStatus").toString()));
//            taskRow.setCleanedText(item.getFields().get("任务Id").toString());
            Object taskId = item.getFields().get("taskId");
            task.setTaskId(taskId == null ? "" : taskId.toString());
            task.setLockExpireTime(item.getFields().get("lockExpireTime").toString());

            task.setTaskId(StringUtils.getString(item.getFields().get("任务Id")));

            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public boolean casLock(SummaryTask task) {
        GetAppTableRecordResp appTableRecord = bitTableService.getAppTableRecord(appToken, tableId, task.getRecordId());
        if (appTableRecord == null) {
            return false;
        }

        Object workId = appTableRecord.getData().getRecord().getFields().get("workId");
        if (workId != null) {
            Object lockExpireTime = appTableRecord.getData().getRecord().getFields().get("lockExpireTime");
            if (lockExpireTime != null && (double) lockExpireTime > System.currentTimeMillis()) {
                return false;
            }
        }

        return true;
    }

    private boolean checkWork(SummaryTask task) {
        GetAppTableRecordResp appTableRecord = bitTableService.getAppTableRecord(appToken, tableId, task.getRecordId());
        if (appTableRecord == null) {
            return false;
        }

        Object workId = appTableRecord.getData().getRecord().getFields().get("workId");
        if (workId != null) {
            Object lockExpireTime = appTableRecord.getData().getRecord().getFields().get("lockExpireTime");
            if (lockExpireTime != null && (double) lockExpireTime > System.currentTimeMillis()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void finish(SummaryTask task, ProcessResult<SummaryFlowStatus, SummaryTaskContext> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("workId", "");
        map.put("lockExpireTime", null);
        map.put("flowStatus", result.getNextStatus().name());
        SummaryTaskContext context = task.getContext();
        if (context != null) {
            if (context.getCleanedText() != null) {
//                map.put("cleanedText", context.getCleanedText());
                map.put("语音识别结果", context.getCleanedText());
            }
        }
        bitTableService.updateAppTableRecord(appToken, tableId, task.getRecordId(), map);
    }
}
