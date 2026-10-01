package com.portfolioai.service;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.exception.ChatProcessingException;
import com.portfolioai.exception.InvalidQuestionException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Singleton;

@Singleton
public class ChatService {

    private final ResumeAssistant resumeAssistant;

    public ChatService(ResumeAssistant resumeAssistant) {
        this.resumeAssistant = resumeAssistant;
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    public ChatResponse processChat(ChatRequest request) {
        validateQuestion(request);

        try {
            String answer = resumeAssistant.chat(request.question());
            return new ChatResponse(answer);
        } catch (RuntimeException _) {
            throw new ChatProcessingException();
        }
    }

    private void validateQuestion(ChatRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            throw new InvalidQuestionException("Question must not be blank");
        }

        if (request.question().length() > ChatRequest.MAX_QUESTION_LENGTH) {
            throw new InvalidQuestionException("Question exceeds maximum length");
        }
    }
}
