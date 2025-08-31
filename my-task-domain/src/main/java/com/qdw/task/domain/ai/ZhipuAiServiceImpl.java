package com.qdw.task.domain.ai;

import ai.z.openapi.core.Constants;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZhipuAiServiceImpl implements IAiService {
    private ZhiPuAiChatModel chatModel;

    private ChatClient chatClient;

    @Value("${spring.ai.zhipuai.api-key}")
    private String apiKey;

    @Autowired
    ChatMemory chatMemory;

    @Resource
    private PgVectorStore pgVectorStore;

    @PostConstruct
    public void init() {

        String zhipu_ai_api_key = System.getenv("ZHIPU_AI_API_KEY");
        System.out.println(zhipu_ai_api_key);
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(apiKey);
        chatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model(Constants.ModelChatGLM4_5)
                .temperature(0.5)
                .maxTokens(1024)
                .build());
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

    }
//    @Resource
//    private PgVectorStore pgVectorStore;

//    @RequestMapping(value = "generate", method = RequestMethod.GET)
    @Override
    public ChatResponse generate(@RequestParam("model") String model, @RequestParam("message") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call().chatResponse();
    }

    @Override
    public Flux<ChatResponse> generateStream(String model, String message) {
        return this.chatClient.prompt()
                .user(message)
                .stream().chatResponse();
    }

    @Override
    public ChatResponse generateRag(String model, String ragTag, String message) {
        log.info("generateRag model:{} ragTag:{} message:{}", model, ragTag, message);
        String SYSTEM_PROMPT = """
                使用文档中的信息提供准确的答案，如果不确定，简单地说你不知道，你的回复必须用中文！
                文档:{documents}
                """;

        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));

        List<Message> messages = new ArrayList<>();
        messages.add(ragMessage);
        messages.add(new UserMessage(message));
//        messages.add(new UserMessage(message + "." + ragMessage));

        log.info("generateRag messages: {}", JSONObject.toJSONString(messages));

        return this.chatClient.prompt()
                .messages(messages)
                .call().chatResponse();
    }

        @Override
    public Flux<ChatResponse> generateStreamRag(String model, String ragTag, String message) {

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        // 指定文档搜索
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);
        String documentCollectors = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));

        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);

        log.info("generateStreamRag messages: {}", JSONObject.toJSONString(messages));
        return this.chatClient.prompt()
                .messages( messages)
//                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .stream().chatResponse();
    }


}
