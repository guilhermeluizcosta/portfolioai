package com.portfolioai.config;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
@Property(name = "langchain4j.open-ai.api-key", value = "test-key")
@Property(name = "PORT", value = "9999")
class ServerPortBindingTest {

    @Inject
    @Property(name = "micronaut.server.port")
    Integer serverPort;

    @Test
    @DisplayName("binds server port from PORT env for Render deployment")
    void bindsPortFromPortEnv() {
        assertEquals(9999, serverPort);
    }
}