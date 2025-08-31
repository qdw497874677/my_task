package com.qdw.task.domain.ai.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RagConfig {

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }


    @Bean
    public SimpleVectorStore vectorStore(@Value("${spring.ai.rag.embed}") String model, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        if ("nomic-embed-text".equalsIgnoreCase(model)) {
            return SimpleVectorStore.builder(embeddingModel).build();
        } else {
//            OpenAiEmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//            return new SimpleVectorStore(embeddingClient);
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    @Bean
    public PgVectorStore pgVectorStore(@Value("${spring.ai.rag.embed}") String model, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate) {
        if ("nomic-embed-text".equalsIgnoreCase(model)) {
            return PgVectorStore.builder(jdbcTemplate, embeddingModel).dimensions(1024).vectorTableName("simple_rag").build();
//            return new PgVectorStore(jdbcTemplate, embeddingClient);
        } else {
//            OpenAiEmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//            return new PgVectorStore(jdbcTemplate, embeddingClient);
            return PgVectorStore.builder(jdbcTemplate, embeddingModel).dimensions(1024).vectorTableName("simple_rag").build();
        }
    }
}
