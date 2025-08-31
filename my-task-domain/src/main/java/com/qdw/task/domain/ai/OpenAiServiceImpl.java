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
//    @Resource
//    private OpenAiChatClient chatClient;
////    @Resource
////    private PgVectorStore pgVectorStore;
//
////    @RequestMapping(value = "generate", method = RequestMethod.GET)
//    @Override
//    public ChatResponse generate(@RequestParam("model") String model, @RequestParam("message") String message) {
//        return chatClient.call(new Prompt(
//                message,
//                OpenAiChatOptions.builder()
//                        .withModel(model)
//                        .build()
//        ));
//    }
//
//    /**
//     * curl http://localhost:8090/api/v1/openai/generate_stream?model=gpt-4o&message=1+1
//     */
////    @RequestMapping(value = "generate_stream", method = RequestMethod.GET)
//    @Override
//    public Flux<ChatResponse> generateStream(@RequestParam("model") String model, @RequestParam("message") String message) {
//        return chatClient.stream(new Prompt(
//                message,
//                OpenAiChatOptions.builder()
//                        .withModel(model)
//                        .build()
//        ));
//    }
//
////    @RequestMapping(value = "generate_stream_rag", method = RequestMethod.GET)
//    @Override
//    public Flux<ChatResponse> generateStreamRag(@RequestParam("model") String model, @RequestParam("ragTag") String ragTag, @RequestParam("message") String message) {
//
//        String SYSTEM_PROMPT = """
//                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
//                If unsure, simply state that you don't know.
//                Another thing you need to note is that your reply must be in Chinese!
//                DOCUMENTS:
//                    {documents}
//                """;
//
//        // 指定文档搜索
//        SearchRequest request = SearchRequest.query(message)
//                .withTopK(5)
//                .withFilterExpression("knowledge == '" + ragTag + "'");
//
////        List<Document> documents = pgVectorStore.similaritySearch(request);
////        String documentCollectors = documents.stream().map(Document::getContent).collect(Collectors.joining());
////        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));
////
////        List<Message> messages = new ArrayList<>();
////        messages.add(new UserMessage(message));
////        messages.add(ragMessage);
////
////        return chatClient.stream(new Prompt(
////                messages,
////                OpenAiChatOptions.builder()
////                        .withModel(model)
////                        .build()
////        ));
//
//        return null;
//    }
}
