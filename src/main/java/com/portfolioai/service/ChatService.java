package com.portfolioai.service;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import jakarta.inject.Singleton;

@Singleton
public class ChatService {

    private final ResumeAssistant resumeAssistant;

    public ChatService(ResumeAssistant resumeAssistant) {
        this.resumeAssistant = resumeAssistant;
    }

    public ChatResponse processChat(ChatRequest request) {
        String answer = resumeAssistant.chat(request.question());
        return new ChatResponse(answer);
    }

}
