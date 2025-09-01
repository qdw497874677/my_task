package com.qdw.task.domain.ai;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

//@Service
public class OpenAiServiceImpl implements IAiService {
    @Override
    public ChatResponse generate(String model, String message) {
        return null;
    }

    @Override
    public Flux<ChatResponse> generateStream(String model, String message) {
        return null;
    }

    @Override
    public ChatResponse generateRag(String model, String ragTag, String message) {
        return null;
    }

    @Override
    public Flux<ChatResponse> generateStreamRag(String model, String ragTag, String message) {
        return null;
    }
}
