package com.qdw.task.domain.ai.image;

import com.qdw.task.api.ai.AiImageProcessService;
import com.qdw.task.domain.feishu.drive.FeishuDriveUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class AiImageProcessServiceImpl implements AiImageProcessService {

    @Autowired
    private IAiImageService aiImageService;
    
    @Autowired
    private FeishuDriveUploadUtil feishuDriveUploadUtil;

    @Override
    public String processImage(String imageUrl, String prompt) {
        byte[] bytes = aiImageService.processImageByUrlToByte("", imageUrl, prompt);

        // 将字节数组转换为图片文件并上传到飞书
        try {
            // 创建临时文件
            File tempFile = createTempFileFromBytes(bytes, "processed_image.png");
            
            // 上传到飞书云盘（这里假设上传到根目录，实际使用时可能需要指定具体目录）
            String fileToken = feishuDriveUploadUtil.uploadFile(tempFile, "processed_image.png", "");
            
            // 删除临时文件
            tempFile.delete();
            
            // 返回文件访问地址（这里需要根据实际的飞书API返回格式进行调整）
            return "https://drive.feishu.cn/file/" + fileToken;
        } catch (IOException e) {
            throw new RuntimeException("处理图片失败", e);
        }
    }
    
    /**
     * 将字节数组转换为临时文件
     * @param bytes 字节数组
     * @param fileName 文件名
     * @return 临时文件
     * @throws IOException IO异常
     */
    private File createTempFileFromBytes(byte[] bytes, String fileName) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), "_" + fileName);
        
        // 将字节数组写入文件
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(bytes);
        }
        
        return tempFile;
    }
}
