package com.qdw.task.api.feishu;

import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import com.lark.oapi.service.drive.v1.model.UploadPartFileResp;
import com.lark.oapi.service.drive.v1.model.UploadFinishFileResp;

import java.io.File;
import java.io.IOException;

public interface IFeishuDriveService {

    /**
     * 分片上传文件-预上传
     * @param fileName 文件名
     * @param parentNode 父节点token
     * @param fileSize 文件大小
     * @return 预上传响应
     */
    UploadPrepareFileResp uploadPrepare(String fileName, String parentNode, int fileSize);

    /**
     * 分片上传文件-上传分片
     * @param uploadId 上传事务ID
     * @param seq 分片序号
     * @param size 分片大小
     * @param file 分片文件
     * @return 上传分片响应
     * @throws IOException IO异常
     */
    UploadPartFileResp uploadPart(String uploadId, int seq, int size, File file) throws IOException;

    /**
     * 分片上传文件-完成上传
     * @param uploadId 上传事务ID
     * @param blockNum 分片数量
     * @return 完成上传响应
     */
    UploadFinishFileResp uploadFinish(String uploadId, int blockNum);
}
