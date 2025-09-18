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

        if (response == null) {
            throw new RuntimeException("图像处理服务返回空响应，请检查API密钥和网络连接");
        }

        AiImageResponse aiImageResponse = JSONObject.parseObject(response, AiImageResponse.class);

        if (aiImageResponse == null || aiImageResponse.getChoices() == null || aiImageResponse.getChoices().isEmpty()) {
            // 从响应中提取文本内容作为备选
            String textContent = imageProcessingService.extractImageDataFromResponse(response);
            return textContent != null ? textContent : "图像处理失败，无法解析API响应";
        }

        try {
            String url = aiImageResponse.getChoices().get(0).getMessage().getImages().get(0).getImageUrl().getUrl();
            imageProcessingService.writeFile(url);
        } catch (Exception e) {
            // 如果无法解析图像URL，尝试提取文本内容
            String textContent = imageProcessingService.extractImageDataFromResponse(response);
            return textContent != null ? textContent : "图像处理成功，但无法获取图像URL: " + e.getMessage();
        }

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

        if (response == null) {
            throw new RuntimeException("图像处理服务返回空响应，请检查API密钥和网络连接");
        }

        AiImageResponse aiImageResponse = JSONObject.parseObject(response, AiImageResponse.class);

        if (aiImageResponse == null || aiImageResponse.getChoices() == null || aiImageResponse.getChoices().isEmpty()) {
            throw new RuntimeException("图像处理失败，无法解析API响应");
        }

        try {
            String url = aiImageResponse.getChoices().get(0).getMessage().getImages().get(0).getImageUrl().getUrl();
            return imageProcessingService.transFile(url);
        } catch (Exception e) {
            throw new RuntimeException("图像处理成功，但无法获取图像URL: " + e.getMessage());
        }
    }


}
