package com.portfolioai.controller;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.service.ChatService;
import io.micronaut.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("ChatController")
class ChatControllerTest {

    private RecordingChatService chatService;
    private ChatController controller;

    @BeforeEach
    void setUp() {
        chatService = new RecordingChatService();
        controller = new ChatController(chatService);
    }

    @Nested
    @DisplayName("When question is blank")
    class WhenQuestionIsBlank {

        @Test
        @DisplayName("Should return 400 for null question")
        void shouldReturn400ForNullQuestion() {
            HttpResponse<ChatResponse> response = controller.chat(new ChatRequest(null));

            assertEquals(400, response.getStatus().getCode());
            assertFalse(chatService.wasCalled());
        }

        @Test
        @DisplayName("Should return 400 for blank question")
        void shouldReturn400ForBlankQuestion() {
            HttpResponse<ChatResponse> response = controller.chat(new ChatRequest("   "));

            assertEquals(400, response.getStatus().getCode());
            assertFalse(chatService.wasCalled());
        }
    }

    @Nested
    @DisplayName("When question exceeds max length")
    class WhenQuestionExceedsMaxLength {

        @Test
        @DisplayName("Should return 400 for question with 2001 characters")
        void shouldReturn400ForQuestionWith2001Characters() {
            String question = "a".repeat(ChatRequest.MAX_QUESTION_LENGTH + 1);

            HttpResponse<ChatResponse> response = controller.chat(new ChatRequest(question));

            assertEquals(400, response.getStatus().getCode());
            assertFalse(chatService.wasCalled());
        }
    }

    @Nested
    @DisplayName("When question is valid")
    class WhenQuestionIsValid {

        @Test
        @DisplayName("Should return 200 for question with exactly 2000 characters")
        void shouldReturn200ForQuestionWithExactly2000Characters() {
            String question = "a".repeat(ChatRequest.MAX_QUESTION_LENGTH);
            ChatResponse expected = new ChatResponse("ok");
            chatService.setNextResponse(expected);

            HttpResponse<ChatResponse> response = controller.chat(new ChatRequest(question));

            assertEquals(200, response.getStatus().getCode());
            assertEquals(expected, response.body());
        }

        @Test
        @DisplayName("Should return 200 with answer from chat service")
        void shouldReturn200WithAnswerFromChatService() {
            ChatRequest request = new ChatRequest("What is your email?");
            ChatResponse expected = new ChatResponse("guilhermelc10@gmail.com");
            chatService.setNextResponse(expected);

            HttpResponse<ChatResponse> response = controller.chat(request);

            assertEquals(200, response.getStatus().getCode());
            assertEquals(expected, response.body());
            assertEquals(request, chatService.lastRequest());
        }
    }

    private static final class RecordingChatService extends ChatService {

        private ChatResponse nextResponse;
        private ChatRequest lastRequest;
        private boolean called;

        RecordingChatService() {
            super(new NoOpResumeAssistant());
        }

        void setNextResponse(ChatResponse nextResponse) {
            this.nextResponse = nextResponse;
        }

        boolean wasCalled() {
            return called;
        }

        ChatRequest lastRequest() {
            return lastRequest;
        }

        @Override
        public ChatResponse processChat(ChatRequest request) {
            called = true;
            lastRequest = request;
            return nextResponse;
        }
    }

    private static final class NoOpResumeAssistant implements ResumeAssistant {

        @Override
        public String chat(String userMessage) {
            return "unused";
        }
    }
}