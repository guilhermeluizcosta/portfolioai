package com.portfolioai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.listener.EmbeddingModelListener;
import dev.langchain4j.model.embedding.onnx.e5smallv2q.E5SmallV2QuantizedEmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmbeddingModelFactory {

    public static final int MODEL_MAX_LENGTH = 512;

    private static final Logger LOG =
            LoggerFactory.getLogger(EmbeddingModelFactory.class);


    private EmbeddingModelFactory() {
    }

    public static EmbeddingModel create(
            EmbeddingModelListener listener) {

        long startNanos = System.nanoTime();

        EmbeddingModel model =
                new E5SmallV2QuantizedEmbeddingModel();

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.info(
                "embedding.model.initialized durationMs={} implementation={}",
                durationMs,
                model.getClass().getSimpleName()
        );

        return model.addListener(listener);
    }

    public static EmbeddingModel create() { return new E5SmallV2QuantizedEmbeddingModel();}
}
