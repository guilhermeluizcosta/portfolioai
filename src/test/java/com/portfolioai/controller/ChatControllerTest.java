package com.portfolioai.controller;

import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ChatController")
class ChatControllerTest {

    private RecordingChatService chatService;
    private ChatController controller;

    @BeforeEach
    void setUp() {
        chatService = new RecordingChatService();
        controller = new ChatController(chatService);
    }

    @Test
    @DisplayName("Should delegate chat processing to ChatService")
    void shouldDelegateToChatService() {
        ChatRequest request = new ChatRequest("What is your email?");
        ChatResponse expected = new ChatResponse("guilhermelc10@gmail.com");
        chatService.setNextResponse(expected);

        ChatResponse response = controller.chat(request);

        assertEquals(expected, response);
        assertEquals(request, chatService.lastRequest());
    }

    private static final class RecordingChatService extends ChatService {

        private ChatResponse nextResponse;
        private ChatRequest lastRequest;

        RecordingChatService() {
            super(userMessage -> "unused");
        }

        void setNextResponse(ChatResponse nextResponse) {
            this.nextResponse = nextResponse;
        }

        ChatRequest lastRequest() {
            return lastRequest;
        }

        @Override
        public ChatResponse processChat(ChatRequest request) {
            lastRequest = request;
            return nextResponse;
        }
    }
}