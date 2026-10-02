package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import com.portfolioai.ai.ResumeAssistant;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class AiConfig {

    @Singleton
    public EmbeddingStore<TextSegment> embeddingStore() {
        return EmbeddingStoreLoader.load(getClass().getClassLoader());
    }

    @Singleton
    public ResumeAssistant resumeAssistant(
            ChatModel chatLanguageModel,
            RagRetrievalProperties ragProperties,
            EmbeddingStore<TextSegment> embeddingStore) {

        EmbeddingModel embeddingModel = EmbeddingModelFactory.create();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(ragProperties.maxResults())
                .minScore(ragProperties.minScore())
                .dynamicFilter(RagCategoryClassifier::filterForQuery)
                .build();

        return AiServices.builder(ResumeAssistant.class)
                .chatModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();

    }
}
