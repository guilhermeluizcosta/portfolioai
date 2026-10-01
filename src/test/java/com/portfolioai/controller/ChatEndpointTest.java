package com.portfolioai.controller;

import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ErrorResponse;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
@DisplayName("POST /api/v1/chat validation")
class ChatEndpointTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @DisplayName("Should return 400 for blank question")
    void shouldReturn400ForBlankQuestion() {
        HttpRequest<ChatRequest> request = HttpRequest.POST("/api/v1/chat", new ChatRequest("   "));

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorResponse.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(
                "Question must not be blank",
                exception.getResponse().getBody(ErrorResponse.class).orElseThrow().error()
        );
    }

    @Test
    @DisplayName("Should return 400 for question with 2001 characters")
    void shouldReturn400ForOversizedQuestion() {
        String question = "a".repeat(ChatRequest.MAX_QUESTION_LENGTH + 1);
        HttpRequest<ChatRequest> request = HttpRequest.POST("/api/v1/chat", new ChatRequest(question));

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorResponse.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(
                "Question exceeds maximum length",
                exception.getResponse().getBody(ErrorResponse.class).orElseThrow().error()
        );
    }
}