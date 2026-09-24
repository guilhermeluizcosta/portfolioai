package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import com.portfolioai.ai.ResumeAssistant;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.e5smallv2q.E5SmallV2QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.net.URL;

@Factory
public class AiConfig {
    @Singleton
    public ResumeAssistant resumeAssistant(ChatModel chatLanguageModel) {

        EmbeddingStore<TextSegment> embeddingStore;
        try {
            URL embeddingsUrl = getClass().getClassLoader().getResource("embeddings.json");
            if (embeddingsUrl == null) {
                throw new IllegalStateException("Arquivo embeddings.json não encontrado.");
            }

            java.nio.file.Path path = java.nio.file.Paths.get(embeddingsUrl.toURI());
            embeddingStore = InMemoryEmbeddingStore.fromFile(path);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar o banco de vetores local", e);
        }

        EmbeddingModel embeddingModel = new E5SmallV2QuantizedEmbeddingModel();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(10)
                .minScore(0.5)
                .build();

        return AiServices.builder(ResumeAssistant.class)
                .chatModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();

    }
}
