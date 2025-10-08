package com.qdw.task.domain.ai;

import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Lazy
public class OpenRouterAiServiceImpl implements IAiService {

    private ChatClient chatClient;

    @Value("${spring.ai.openroute.api-key}")
    private String apiKey;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private PgVectorStore pgVectorStore;

    @Override
    public ChatResponse generate(String model, String message) {
        log.info("Generating response with model: {}, message: {}", model, message);
        throw new UnsupportedOperationException("OpenRouter service not implemented yet - use ZhipuAI instead");
    }

    @Override
    public Flux<ChatResponse> generateStream(String model, String message) {
        log.info("Generating stream response with model: {}, message: {}", model, message);
        return Flux.error(new UnsupportedOperationException("OpenRouter service not implemented yet - use ZhipuAI instead"));
    }

    @Override
    public ChatResponse generateRag(String model, String ragTag, String message) {
        log.info("Generating RAG response with model: {}, ragTag: {}, message: {}", model, ragTag, message);
        throw new UnsupportedOperationException("OpenRouter service not implemented yet - use ZhipuAI instead");
    }

    @Override
    public Flux<ChatResponse> generateStreamRag(String model, String ragTag, String message) {
        log.info("Generating stream RAG response with model: {}, ragTag: {}, message: {}", model, ragTag, message);
        return Flux.error(new UnsupportedOperationException("OpenRouter service not implemented yet - use ZhipuAI instead"));
    }
}