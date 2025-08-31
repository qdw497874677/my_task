package com.qdw.task.domain.task.yt;

import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.GetAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordResp;
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordRespBody;
import com.qdw.task.api.feishu.IFeishuBitTableService;
import com.qdw.task.api.feishu.IFeishuService;
import com.qdw.task.domain.task.base.BaseTaskMapper;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskTextRow;
import com.qdw.task.domain.task.summary.SummaryTask;
import com.qdw.task.domain.common.StringUtils;
import com.qdw.task.domain.task.base.TaskLinkRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class YtMapper implements BaseTaskMapper<YtFlowStatus, YtTaskContext, YtTask> {

    @Autowired
    private IFeishuService feishuService;
    @Autowired
    private IFeishuBitTableService bitTableService;

    private String appToken = "UvQIbBxDda125ZsphTocTVVInve";
    private String tableId = "tblCoz4bRa2FDlky";

    private YtTask task = new YtTask();

//    public SummaryMapper(SummaryTask task) {
//        this.task = task;
//    }

    @Override
    public List<YtTask> getPending() {
        List<YtTask> taskList = new ArrayList<>();
        YtFlowStatus completed = task.getCompleted();
        YtFlowStatus errorStatus = task.getErrorStatus();
        List<String> statusList = new ArrayList<>();
        for (YtFlowStatus value : YtFlowStatus.values()) {
            if (!completed.equals(value) && !errorStatus.equals(value)) {
                statusList.add(value.name());
            }
        }

        SearchAppTableRecordResp searchAppTableRecordResp = bitTableService.queryPending(appToken, tableId, "flowStatus", statusList.toArray(new String[0]));
        SearchAppTableRecordRespBody data = searchAppTableRecordResp.getData();
        AppTableRecord[] items = data.getItems();
        for (AppTableRecord item : items) {
            YtTask task = new YtTask();
            task.setRecordId(item.getRecordId());

            YtTaskContext context = new YtTaskContext();
            task.setContext(context);
            context.setVideoUrl(StringUtils.parseArray(item.getFields().get("视频地址"), TaskLinkRow.class).get(0).getLink());

            List<TaskTextRow> videoDownloadTaskId = StringUtils.parseArray(item.getFields().get("视频下载taskId"), TaskTextRow.class);
            if (!CollectionUtils.isEmpty(videoDownloadTaskId)) {
                context.setDownloadVideoTaskId(videoDownloadTaskId.get(0).getText());
            }
            task.setStatus(YtFlowStatus.valueOf(item.getFields().get("flowStatus").toString()));
//            taskRow.setCleanedText(item.getFields().get("任务Id").toString());
            Object taskId = item.getFields().get("taskId");
            task.setTaskId(taskId == null ? "" : taskId.toString());
            task.setLockExpireTime(StringUtils.getString(item.getFields().get("lockExpireTime")));

            task.setTaskId(StringUtils.getString(item.getFields().get("任务Id")));

            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public boolean casLock(YtTask task) {
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
        Map<String, Object> map = new HashMap<>();
        map.put("workId", UUID.randomUUID());
        map.put("lockExpireTime", System.currentTimeMillis());
        bitTableService.updateAppTableRecord(appToken, tableId, task.getRecordId(), map);
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
    public void finish(YtTask task, ProcessResult<YtFlowStatus, YtTaskContext> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("workId", "");
//        map.put("lockExpireTime", "");
        map.put("flowStatus", result.getNextStatus().name());
        map.put("errorMsg", task.getErrorMsg());
        YtTaskContext context = task.getContext();
        if (context != null) {
            if (context.getDownloadVideoUrl() != null) {
                map.put("视频下载地址", context.getDownloadVideoUrl());
            }
            if (context.getDownloadVideoTaskId() != null) {
                map.put("视频下载taskId", context.getDownloadVideoTaskId());
            }

            if (context.getDownloadAudioUrl() != null) {
                map.put("音频下载地址", context.getDownloadAudioUrl());
            }
            if (context.getDownloadAudioTaskId() != null) {
                map.put("音频下载taskId", context.getDownloadVideoTaskId());
            }
            if (context.getTitle() != null) {
                map.put("标题", context.getTitle());
            }
        }
        bitTableService.updateAppTableRecord(appToken, tableId, task.getRecordId(), map);
    }
}
