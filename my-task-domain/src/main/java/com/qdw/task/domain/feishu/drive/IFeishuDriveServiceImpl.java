package com.qdw.task.domain.feishu.drive;

import com.lark.oapi.Client;
import com.lark.oapi.service.drive.v1.model.FileUploadInfo;
import com.lark.oapi.service.drive.v1.model.UploadFinishFileReq;
import com.lark.oapi.service.drive.v1.model.UploadFinishFileReqBody;
import com.lark.oapi.service.drive.v1.model.UploadFinishFileResp;
import com.lark.oapi.service.drive.v1.model.UploadPartFileReq;
import com.lark.oapi.service.drive.v1.model.UploadPartFileReqBody;
import com.lark.oapi.service.drive.v1.model.UploadPartFileResp;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileReq;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import com.qdw.task.api.feishu.IFeishuDriveService;
import com.qdw.task.api.feishu.IFeishuService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class IFeishuDriveServiceImpl implements IFeishuDriveService {

    @Autowired
    private IFeishuService feishuService;

    private Client client;

    @PostConstruct
    public void init() {
        client = feishuService.getClient();
    }

    @Override
    public UploadPrepareFileResp uploadPrepare(String fileName, String parentNode, int fileSize) {
        // 创建请求对象
        UploadPrepareFileReq req = UploadPrepareFileReq.newBuilder()
                .fileUploadInfo(FileUploadInfo.newBuilder()
                        .fileName(fileName)
                        .parentType("explorer")
                        .parentNode(parentNode)
                        .size(fileSize)
                        .build())
                .build();

        // 发起请求
        UploadPrepareFileResp resp = null;
        try {
            resp = client.drive().v1().file().uploadPrepare(req);
        } catch (Exception e) {
            System.err.println("uploadPrepare error: " + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if (!resp.success()) {
            System.err.println(String.format("code:%s,msg:%s,reqId:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return null;
        }

        return resp;
    }

    @Override
    public UploadPartFileResp uploadPart(String uploadId, int seq, int size, File file) throws IOException {
        // 创建请求对象
        UploadPartFileReq req = UploadPartFileReq.newBuilder()
                .uploadPartFileReqBody(
                        UploadPartFileReqBody.newBuilder()
                                .uploadId(uploadId)
                                .seq(seq)
                                .size(size)
                                .file(file).build()
                )
                .build();

        // 发起请求
        UploadPartFileResp resp = null;
        try {
            resp = client.drive().v1().file().uploadPart(req);
        } catch (Exception e) {
            System.err.println("uploadPart error: " + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if (!resp.success()) {
            System.err.println(String.format("code:%s,msg:%s,reqId:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return null;
        }

        return resp;
    }

    @Override
    public UploadFinishFileResp uploadFinish(String uploadId, int blockNum) {
        // 创建请求对象
        UploadFinishFileReq req = UploadFinishFileReq.newBuilder()
                .uploadFinishFileReqBody(
                        UploadFinishFileReqBody.newBuilder()
                                .uploadId(uploadId)
                                .blockNum(blockNum)
                                .build()
                )
                .build();

        // 发起请求
        UploadFinishFileResp resp = null;
        try {
            resp = client.drive().v1().file().uploadFinish(req);
        } catch (Exception e) {
            System.err.println("uploadFinish error: " + e.getMessage());
            return null;
        }

        // 处理服务端错误
        if (!resp.success()) {
            System.err.println(String.format("code:%s,msg:%s,reqId:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return null;
        }

        return resp;
    }
}
