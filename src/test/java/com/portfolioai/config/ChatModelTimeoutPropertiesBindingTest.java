package com.portfolioai.config;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
class ChatModelTimeoutPropertiesBindingTest {

    @Inject
    @Property(name = "langchain4j.open-ai.chat-model.timeout")
    Duration chatModelTimeout;

    @Test
    @DisplayName("binds chat model timeout below Portfolio proxy limit")
    void bindsChatModelTimeout() {
        assertEquals(Duration.ofSeconds(90), chatModelTimeout);
    }
}
