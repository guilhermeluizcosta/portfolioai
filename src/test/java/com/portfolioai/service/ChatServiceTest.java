package com.portfolioai.service;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ChatService")
class ChatServiceTest {

    private RecordingResumeAssistant resumeAssistant;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        resumeAssistant = new RecordingResumeAssistant();
        chatService = new ChatService(resumeAssistant);
    }

    @Nested
    @DisplayName("When processing chat")
    class WhenProcessingChat {

        @Test
        @DisplayName("Should delegate to ResumeAssistant and wrap answer in ChatResponse")
        void shouldDelegateToResumeAssistant() {
            String question = "What technologies do you use?";
            resumeAssistant.setNextAnswer("Java and Micronaut");

            ChatResponse response = chatService.processChat(new ChatRequest(question));

            assertEquals("Java and Micronaut", response.answer());
            assertEquals(question, resumeAssistant.lastQuestion());
        }
    }

    private static final class RecordingResumeAssistant implements ResumeAssistant {

        private String nextAnswer;
        private String lastQuestion;

        void setNextAnswer(String nextAnswer) {
            this.nextAnswer = nextAnswer;
        }

        String lastQuestion() {
            return lastQuestion;
        }

        @Override
        public String chat(String userMessage) {
            lastQuestion = userMessage;
            return nextAnswer;
        }
    }
}