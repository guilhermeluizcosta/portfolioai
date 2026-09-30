package com.portfolioai.health;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
class HealthEndpointTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @DisplayName("GET /health returns 200 with UP status")
    void healthReturnsUp() {
        HttpResponse<String> response = client.toBlocking().exchange("/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().contains("UP"));
    }

    @Test
    @DisplayName("GET /health reports embedding store as UP")
    void healthReportsEmbeddingStore() {
        HttpResponse<String> response = client.toBlocking().exchange("/health", String.class);

        assertTrue(response.body().contains("embeddingStore"));
        assertTrue(response.body().contains("UP"));
    }
}
