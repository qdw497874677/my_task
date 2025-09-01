package com.qdw.task.domain.ai;

import com.alibaba.fastjson.JSONObject;
import com.qdw.task.domain.ai.image.AiImageResponse;
import com.qdw.task.domain.ai.image.IAiImageService;
import com.qdw.task.domain.image.ImageProcessingService;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class IAiImageServiceImpl implements IAiImageService {


    @Autowired
    private ImageProcessingService imageProcessingService;

    @Value("${spring.ai.openroute.api-key:}")
    private String openRouterApiKey;

    @Override
    public ChatResponse processImage(String model, String base64Image, String prompt) {
        // 获取API密钥（实际项目中应从配置文件或环境变量中获取）
        String apiKey = openRouterApiKey;
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is not configured");
        }

        // 调用图像处理服务
        String response = imageProcessingService.processImage(apiKey, base64Image, prompt);

        // 从响应中提取图像数据
        String imageData = imageProcessingService.extractImageDataFromResponse(response);

        // 创建ChatResponse对象并返回
        if (imageData != null) {
            // 构造包含图像数据的响应
            List<Generation> generations = new ArrayList<>();
            Generation generation = new Generation(new AssistantMessage(imageData));
            generations.add(generation);

            return new ChatResponse(generations);
        }

        return null;
    }

    @Override
    public Flux<ChatResponse> processImageStream(String model, String base64Image, String prompt) {
        // 流式图像处理暂时不实现
        return null;
    }

    @Override
    public String processImageByUrl(String model, String imageUrl, String prompt) {

        // 获取API密钥（实际项目中应从配置文件或环境变量中获取）
        String apiKey = openRouterApiKey;
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is not configured");
        }

        // 调用图像处理服务
        String response = imageProcessingService.processImageByUrl(apiKey, imageUrl, prompt);

        AiImageResponse aiImageResponse = JSONObject.parseObject(response, AiImageResponse.class);

        // 从响应中提取图像数据
        String imageData = imageProcessingService.extractImageDataFromResponse(response);

        String url = aiImageResponse.getChoices().get(0).getMessage().getImages().get(0).getImageUrl().getUrl();
        imageProcessingService.writeFile(url);

        return null;
    }

    @Override
    public byte[] processImageByUrlToByte(String model, String imageUrl, String prompt) {
        // 获取API密钥（实际项目中应从配置文件或环境变量中获取）
        String apiKey = openRouterApiKey;
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is not configured");
        }

        // 调用图像处理服务
        String response = imageProcessingService.processImageByUrl(apiKey, imageUrl, prompt);

        AiImageResponse aiImageResponse = JSONObject.parseObject(response, AiImageResponse.class);

        String url = aiImageResponse.getChoices().get(0).getMessage().getImages().get(0).getImageUrl().getUrl();
        return imageProcessingService.transFile(url);

    }


}
