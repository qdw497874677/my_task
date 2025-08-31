package com.qdw.task.domain.feishu.drive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class FeishuDriveExampleService {

    @Autowired
    private FeishuDriveUploadUtil feishuDriveUploadUtil;

    /**
     * 示例：上传文件到飞书云空间
     * @param filePath 本地文件路径
     * @param fileName 上传后的文件名
     * @param folderToken 飞书云空间文件夹token
     */
    public void uploadFileExample(String filePath, String fileName, String folderToken) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("文件不存在: " + filePath);
                return;
            }

            String fileToken = feishuDriveUploadUtil.uploadFile(file, fileName, folderToken);
            System.out.println("文件上传成功，fileToken: " + fileToken);
        } catch (IOException e) {
            System.err.println("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("文件上传异常: " + e.getMessage());
        }
    }
}