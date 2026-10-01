package com.portfolioai.openapi;

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
@DisplayName("Swagger UI at /docs")
class OpenApiDocsEndpointTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    @DisplayName("GET /docs/index.html returns 200")
    void docsIndexReturnsOk() {
        HttpResponse<String> response = client.toBlocking().exchange("/docs/index.html", String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.body().contains("swagger"), "Swagger UI page must be served");
    }
}