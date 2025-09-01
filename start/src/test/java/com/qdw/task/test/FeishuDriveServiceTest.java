package com.qdw.task.test;

import com.qdw.task.domain.feishu.drive.FeishuDriveUploadUtil;
import com.qdw.task.domain.feishu.drive.IFeishuDriveServiceImpl;
import com.lark.oapi.service.drive.v1.model.UploadPrepareFileResp;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeishuDriveServiceTest {

    @Autowired
    private FeishuDriveUploadUtil feishuDriveUploadUtil;
    
    @Autowired
    private IFeishuDriveServiceImpl feishuDriveService;

    @Test
    void testUploadUtil() {
        try {
            // 创建一个测试文件
            File testFile = createTestFile();
            
            // 使用工具类上传文件
            // 注意：需要替换为实际的文件夹token
            String fileToken = feishuDriveUploadUtil.uploadFile(
                testFile, 
                "test-upload.txt", 
                "ZVd4fH8TulAiFxdqw2OclEG6nUg"  // 需要替换为实际的文件夹token
            );
            
            System.out.println("上传成功，文件token: " + fileToken);
            
            // 删除测试文件
            boolean deleted = testFile.delete();
            if (!deleted) {
                System.err.println("警告：测试文件删除失败");
            }
        } catch (IOException e) {
            System.err.println("上传失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("上传异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    void testUploadPrepare() {
        try {
            // 测试预上传功能
            UploadPrepareFileResp resp = feishuDriveService.uploadPrepare(
                "test-prepare.txt", 
                "fldbcO1UuPz8VwnpPx5a92abcef",  // 需要替换为实际的文件夹token
                1024
            );
            
            if (resp != null && resp.success()) {
                System.out.println("预上传成功");
                System.out.println("Upload ID: " + resp.getData().getUploadId());
                System.out.println("Block size: " + resp.getData().getBlockSize());
                System.out.println("Block num: " + resp.getData().getBlockNum());
            } else {
                System.err.println("预上传失败");
                if (resp != null) {
                    System.err.println("错误码: " + resp.getCode());
                    System.err.println("错误信息: " + resp.getMsg());
                }
            }
        } catch (Exception e) {
            System.err.println("预上传异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建测试文件
     */
    private File createTestFile() throws IOException {
        // 创建临时测试文件
        File testFile = File.createTempFile("feishu_test_", ".txt");
        
        // 写入测试内容
        try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
            writer.write("这是一个用于测试飞书云空间分片上传功能的文件。\n");
            writer.write("文件创建时间: " + new java.util.Date() + "\n");
            writer.write("测试内容...\n");
        }
        
        return testFile;
    }
}
