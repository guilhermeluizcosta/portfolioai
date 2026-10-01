package com.portfolioai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.e5smallv2q.E5SmallV2QuantizedEmbeddingModel;

public final class EmbeddingModelFactory {

    public static final int MODEL_MAX_LENGTH = 512;

    private EmbeddingModelFactory() {
    }

    public static EmbeddingModel create() {
        return new E5SmallV2QuantizedEmbeddingModel();
    }
}
