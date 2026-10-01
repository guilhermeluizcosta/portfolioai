package com.portfolioai.controller;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ErrorResponse;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
@DisplayName("POST /api/v1/chat processing failure")
class ChatEndpointFailureTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @DisplayName("Should return 500 with structured error")
    void shouldReturn500WithStructuredError() {
        HttpRequest<ChatRequest> request = HttpRequest.POST("/api/v1/chat", new ChatRequest("What is your email?"));

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorResponse.class)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals(
                "Unable to process your question",
                exception.getResponse().getBody(ErrorResponse.class).orElseThrow().error()
        );
    }

    @MockBean(ResumeAssistant.class)
    ResumeAssistant resumeAssistant() {
        return userMessage -> {
            throw new RuntimeException("Groq API key invalid: sk-secret");
        };
    }
}