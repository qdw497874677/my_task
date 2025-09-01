package com.qdw.task.domain.ai.image;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface IAiImageService {

    /**
     * 图像处理方法
     * @param model 模型名称
     * @param base64Image base64编码的图像
     * @param prompt 提示词
     * @return 处理结果
     */
    ChatResponse processImage(String model, String base64Image, String prompt);

    /**
     * 流式图像处理方法
     * @param model 模型名称
     * @param base64Image base64编码的图像
     * @param prompt 提示词
     * @return 流式处理结果
     */
    Flux<ChatResponse> processImageStream(String model, String base64Image, String prompt);

    /**
     * 通过URL处理图像方法
     * @param model 模型名称
     * @param imageUrl 图像URL地址
     * @param prompt 提示词
     * @return 处理结果 文件下载地址
     */
    String processImageByUrl(String model, String imageUrl, String prompt);


    byte[] processImageByUrlToByte(String model, String imageUrl, String prompt);

}
