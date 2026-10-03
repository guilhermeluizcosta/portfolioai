package com.portfolioai.log;

import dev.langchain4j.model.embedding.listener.EmbeddingModelErrorContext;
import dev.langchain4j.model.embedding.listener.EmbeddingModelListener;
import dev.langchain4j.model.embedding.listener.EmbeddingModelRequestContext;
import dev.langchain4j.model.embedding.listener.EmbeddingModelResponseContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LoggingEmbeddingModelListener
        implements EmbeddingModelListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(LoggingEmbeddingModelListener.class);

    @Override
    public void onRequest(EmbeddingModelRequestContext context) {

        context.attributes().put(
                "startNanos",
                System.nanoTime()
        );

        LOG.debug(
                "rag.embedding.start segments={}",
                context.textSegments() != null
                        ? context.textSegments().size()
                        : 0
        );
    }

    @Override
    public void onResponse(EmbeddingModelResponseContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.debug(
                "rag.embedding.success durationMs={} model={}",
                durationMs,
                context.embeddingModel().modelName()
        );
    }

    @Override
    public void onError(EmbeddingModelErrorContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.error(
                "rag.embedding.failed durationMs={} exceptionType={} message={}",
                durationMs,
                context.error().getClass().getName(),
                context.error().getMessage(),
                context.error()
        );
    }
}