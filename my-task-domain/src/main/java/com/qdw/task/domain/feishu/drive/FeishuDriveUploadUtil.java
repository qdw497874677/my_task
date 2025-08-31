package com.qdw.task.domain.feishu.drive;

import com.lark.oapi.service.drive.v1.model.UploadFinishFileResp;
import com.lark.oapi.service.drive.v1.model.UploadPartFileResp;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FeishuDriveUploadUtil {

    @Autowired
    private IFeishuDriveServiceImpl feishuDriveService;

    private static final int BLOCK_SIZE = 4 * 1024 * 1024; // 4MB

    /**
     * 分片上传文件
     * @param file 要上传的文件
     * @param fileName 文件名
     * @param parentNode 父节点token
     * @return 文件token
     * @throws IOException IO异常
     */
    public String uploadFile(File file, String fileName, String parentNode) throws IOException {
        long fileSize = file.length();
        
        // 1. 预上传
        UploadPrepareFileResp prepareResp = feishuDriveService.uploadPrepare(fileName, parentNode, (int) fileSize);
        if (prepareResp == null || !prepareResp.success()) {
            throw new RuntimeException("预上传失败");
        }
        
        String uploadId = prepareResp.getData().getUploadId();
        int blockNum = prepareResp.getData().getBlockNum();
        
        // 2. 上传分片
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BLOCK_SIZE];
            for (int i = 0; i < blockNum; i++) {
                int bytesRead = fis.read(buffer);
                if (bytesRead <= 0) {
                    break;
                }
                
                // 创建临时文件存储当前分片
                File partFile = createTempFile(buffer, bytesRead, i);
                
                // 上传分片（暂时不使用校验和）
                UploadPartFileResp partResp = feishuDriveService.uploadPart(
                    uploadId, i, bytesRead, partFile);
                    
                if (partResp == null || !partResp.success()) {
                    throw new RuntimeException("上传分片失败: " + i);
                }
                
                // 删除临时文件
                partFile.delete();
            }
        }
        
        // 3. 完成上传
        UploadFinishFileResp finishResp = feishuDriveService.uploadFinish(uploadId, blockNum);
        if (finishResp == null || !finishResp.success()) {
            throw new RuntimeException("完成上传失败");
        }
        
        return finishResp.getData().getFileToken();
    }
    
    /**
     * 创建临时文件存储分片数据
     */
    private File createTempFile(byte[] data, int length, int index) throws IOException {
        File tempFile = File.createTempFile("feishu_upload_part_" + index + "_", ".tmp");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
            fos.write(data, 0, length);
        }
        return tempFile;
    }
}