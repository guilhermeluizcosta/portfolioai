package com.portfolioai.dto;
import io.micronaut.context.annotation.Property;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
class ErrorResponseSerdeTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    @DisplayName("ErrorResponse serializes to {\"error\": \"...\"} JSON")
    void serializesToErrorJson() throws IOException {
        String json = objectMapper.writeValueAsString
                (new ErrorResponse("Question must not be blank"));
        
        assertEquals("{\"error\":\"Question must not be blank\"}", json);
    }
}
