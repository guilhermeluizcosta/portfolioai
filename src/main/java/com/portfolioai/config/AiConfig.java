package com.portfolioai.config;

import dev.langchain4j.model.chat.ChatModel;
import com.portfolioai.ai.ResumeAssistant;
import dev.langchain4j.service.AiServices;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class AiConfig {
    @Singleton
    public ResumeAssistant resumeAssistant(ChatModel chatLanguageModel) {
        return AiServices.builder(ResumeAssistant.class)
                .chatModel(chatLanguageModel)
                // TODO Criar retrievalAugmentor
                .build();
    }
}
