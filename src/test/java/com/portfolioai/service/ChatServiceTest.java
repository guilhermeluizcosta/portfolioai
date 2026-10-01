package com.portfolioai.service;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.exception.ChatProcessingException;
import com.portfolioai.exception.InvalidQuestionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("When question is blank")
    class WhenQuestionIsBlank {

        @Test
        @DisplayName("Should reject null question")
        void shouldRejectNullQuestion() {
            InvalidQuestionException exception = assertThrows(
                    InvalidQuestionException.class,
                    () -> chatService.processChat(new ChatRequest(null))
            );

            assertEquals("Question must not be blank", exception.getMessage());
            assertEquals(false, resumeAssistant.wasCalled());
        }

        @Test
        @DisplayName("Should reject blank question")
        void shouldRejectBlankQuestion() {
            InvalidQuestionException exception = assertThrows(
                    InvalidQuestionException.class,
                    () -> chatService.processChat(new ChatRequest("   "))
            );

            assertEquals("Question must not be blank", exception.getMessage());
            assertEquals(false, resumeAssistant.wasCalled());
        }
    }

    @Nested
    @DisplayName("When question exceeds max length")
    class WhenQuestionExceedsMaxLength {

        @Test
        @DisplayName("Should reject question with 2001 characters")
        void shouldRejectQuestionWith2001Characters() {
            String question = "a".repeat(ChatRequest.MAX_QUESTION_LENGTH + 1);

            InvalidQuestionException exception = assertThrows(
                    InvalidQuestionException.class,
                    () -> chatService.processChat(new ChatRequest(question))
            );

            assertEquals("Question exceeds maximum length", exception.getMessage());
            assertEquals(false, resumeAssistant.wasCalled());
        }
    }

    @Nested
    @DisplayName("When ResumeAssistant fails")
    class WhenResumeAssistantFails {

        @Test
        @DisplayName("Should wrap failure without exposing internal details")
        void shouldWrapFailureWithoutExposingInternalDetails() {
            resumeAssistant.setNextFailure(new RuntimeException("Groq API key invalid: sk-secret"));

            ChatProcessingException exception = assertThrows(
                    ChatProcessingException.class,
                    () -> chatService.processChat(new ChatRequest("What is your email?"))
            );

            assertEquals("Unable to process your question", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When question is valid")
    class WhenQuestionIsValid {

        @Test
        @DisplayName("Should accept question with exactly 2000 characters")
        void shouldAcceptQuestionWithExactly2000Characters() {
            String question = "a".repeat(ChatRequest.MAX_QUESTION_LENGTH);
            resumeAssistant.setNextAnswer("ok");

            ChatResponse response = chatService.processChat(new ChatRequest(question));

            assertEquals("ok", response.answer());
            assertEquals(question, resumeAssistant.lastQuestion());
        }

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
        private RuntimeException nextFailure;
        private String lastQuestion;
        private boolean called;

        void setNextAnswer(String nextAnswer) {
            this.nextAnswer = nextAnswer;
        }

        void setNextFailure(RuntimeException nextFailure) {
            this.nextFailure = nextFailure;
        }

        String lastQuestion() {
            return lastQuestion;
        }

        boolean wasCalled() {
            return called;
        }

        @Override
        public String chat(String userMessage) {
            called = true;
            lastQuestion = userMessage;
            if (nextFailure != null) {
                throw nextFailure;
            }
            return nextAnswer;
        }
    }
}