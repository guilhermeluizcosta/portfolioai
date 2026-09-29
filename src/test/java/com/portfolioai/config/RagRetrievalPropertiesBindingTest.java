package com.portfolioai.config;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
class RagRetrievalPropertiesBindingTest {

    @Inject
    RagRetrievalProperties properties;

    @Test
    @DisplayName("binds maxResults and minScore from application configuration")
    void bindsFromConfiguration() {
        assertEquals(4, properties.maxResults());
        assertEquals(0.65, properties.minScore());
    }
}