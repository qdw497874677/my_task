package com.qdw.task.test;

import com.alibaba.fastjson.JSONObject;
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
import reactor.core.publisher.Flux;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AiServiceTest {

    @Autowired
    ZhipuAiServiceImpl zhipuAiService;

    @Autowired
    IRAGService ragService;

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


}
