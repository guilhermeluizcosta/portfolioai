package com.portfolioai.controller;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
@DisplayName("POST /api/v1/chat success")
class ChatEndpointSuccessTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @DisplayName("Should return 200 with answer from ResumeAssistant")
    void shouldReturn200WithAnswer() {
        HttpRequest<ChatRequest> request = HttpRequest.POST("/api/v1/chat", new ChatRequest("What is your email?"));
        HttpResponse<ChatResponse> response = client.toBlocking().exchange(request, ChatResponse.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("guilhermelc10@gmail.com", response.body().answer());
    }

    @MockBean(ResumeAssistant.class)
    ResumeAssistant resumeAssistant() {
        return userMessage -> "guilhermelc10@gmail.com";
    }
}