package com.qdw.task.domain.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImageProcessingService {

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL_NAME = "google/gemini-2.5-flash-image-preview:free";

    /**
     * 调用图像处理API
     * @param apiKey API密钥
     * @param base64Image base64编码的图像
     * @param prompt 提示词
     * @return API响应结果
     */
    public String processImage(String apiKey, String base64Image, String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);
        
        // 构建消息内容
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        // 构建内容数组
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/png;base64," + base64Image);
        imageContent.put("image_url", imageUrl);
        
        Map<String, Object>[] contentArray = new Map[2];
        contentArray[0] = textContent;
        contentArray[1] = imageContent;
        
        message.put("content", contentArray);
        
        Map<String, Object>[] messages = new Map[1];
        messages[0] = message;
        
        requestBody.put("messages", messages);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_API_URL, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String processImageByUrl(String apiKey, String url, String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);

        // 构建消息内容
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        // 构建内容数组
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", url);
        imageContent.put("image_url", imageUrl);

        Map<String, Object>[] contentArray = new Map[2];
        contentArray[0] = textContent;
        contentArray[1] = imageContent;

        message.put("content", contentArray);

        Map<String, Object>[] messages = new Map[1];
        messages[0] = message;
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_API_URL, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从API响应中提取图像数据
     * @param response API响应
     * @return base64编码的图像数据
     */
    public String extractImageDataFromResponse(String response) {
        if (response == null) {
            return null;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            
            // 提取响应中的图像数据
            JsonNode choicesNode = rootNode.get("choices");
            if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).get("message");
                if (messageNode != null) {
                    JsonNode contentNode = messageNode.get("content");
                    if (contentNode != null) {
                        String content = contentNode.asText();
                        // 如果内容是JSON格式，尝试解析其中的图像数据
                        if (content.contains("data:image")) {
                            return extractBase64FromDataUrl(content);
                        }
                        return content;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 从data URL中提取base64数据
     * @param dataUrl data URL
     * @return base64数据
     */
    private String extractBase64FromDataUrl(String dataUrl) {
        int commaIndex = dataUrl.indexOf(",");
        if (commaIndex != -1 && commaIndex < dataUrl.length() - 1) {
            return dataUrl.substring(commaIndex + 1);
        }
        return dataUrl;
    }

    public void writeFile(String dataUri) {

        try {
            // 这里放你的完整 data URI，也可以从文件/网络读

            // 1. 去掉前缀
            String base64 = dataUri.substring(dataUri.indexOf(",") + 1);

            // 2. 解码
            byte[] bytes = Base64.getDecoder().decode(base64);

            // 3. 写出文件
            Path out = Paths.get("output.png");
            Files.write(out, bytes);

            log.info("已生成 " + out.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 转换 data URI 为 byte[]
     * @param dataUri
     * @return
     */
    public byte[] transFile(String dataUri) {

        try {
            // 这里放你的完整 data URI，也可以从文件/网络读

            // 1. 去掉前缀
            String base64 = dataUri.substring(dataUri.indexOf(",") + 1);

            // 2. 解码
            return Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}