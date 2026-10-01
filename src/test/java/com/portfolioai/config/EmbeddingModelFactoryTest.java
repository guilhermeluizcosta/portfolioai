package com.portfolioai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("EmbeddingModelFactory")
class EmbeddingModelFactoryTest {

    @Test
    @DisplayName("create returns E5-small-v2 quantized model with 384 dimensions")
    void createReturnsModelWithExpectedDimensions() {
        EmbeddingModel model = EmbeddingModelFactory.create();

        assertNotNull(model);
        assertEquals(384, model.dimension());
    }
}
