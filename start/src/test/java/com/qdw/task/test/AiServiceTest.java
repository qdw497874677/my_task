package com.qdw.task.test;

import com.alibaba.fastjson.JSONObject;
import com.qdw.task.api.ai.AiImageProcessService;
import com.qdw.task.domain.ai.image.IAiImageService;
import com.qdw.task.domain.ai.IRAGService;
import com.qdw.task.domain.ai.ZhipuAiServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AiServiceTest {

    @Autowired
    ZhipuAiServiceImpl zhipuAiService;

    @Autowired
    IRAGService ragService;

    @Autowired
    private IAiImageService aiImageService;

    @Autowired
    private AiImageProcessService aiImageProcessService;

    @Test
    public void testOpenAi() {
        ChatResponse response = zhipuAiService.generate("glm-4.5", "你好");
        System.out.println(JSONObject.toJSONString(response));
    }

    @Test
    public void testRag() {
        List<Document> documents = ragService.vectorStore();
        System.out.println(JSONObject.toJSONString(documents));
    }

    @Test
    public void testRag2() {
        TikaDocumentReader documentReader = new TikaDocumentReader("./file.txt");
        List<Document> documents = documentReader.get();
        ragService.uploadDocument("testTag", documents);
    }

    @Test
    public void testRag3() {
        ChatResponse chatResponse = zhipuAiService.generateRag("", "testTag", "权晓怡几岁了");
        System.out.println(JSONObject.toJSONString(chatResponse.getResult()));
    }

    @Test
    public void testImage() {
        String s = aiImageService.processImageByUrl("", "https://i.imgs.ovh/2025/09/01/wC3aq.webp", "Replace Lawson with KFC");

        System.out.println(s);
    }

    @Test
    public void testImage2() {
        String s = aiImageProcessService.processImage("https://i.imgs.ovh/2025/09/01/wC3aq.webp", "Replace Lawson with KFC");

        System.out.println(s);
    }



}
