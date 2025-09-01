package com.qdw.task.api.image;

import com.qdw.task.domain.ai.image.IAiImageService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private IAiImageService aiImageService;

    /**
     * 处理上传的图像
     * @param image 图像文件
     * @param prompt 提示词
     * @return 处理结果
     */
    @PostMapping("/process")
    public String processImage(@RequestParam("image") MultipartFile image,
                               @RequestParam("prompt") String prompt) {
        try {
            // 将图像转换为base64编码
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 调用图像处理服务
            ChatResponse response = aiImageService.processImage("gemini-2.5-flash-image-preview", base64Image, prompt);

            if (response != null) {
                return response.toString();
            } else {
                return "图像处理失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "处理图像时发生错误: " + e.getMessage();
        }
    }
}