package com.portfolioai.log;

import dev.langchain4j.rag.content.retriever.listener.ContentRetrieverErrorContext;
import dev.langchain4j.rag.content.retriever.listener.ContentRetrieverListener;
import dev.langchain4j.rag.content.retriever.listener.ContentRetrieverRequestContext;
import dev.langchain4j.rag.content.retriever.listener.ContentRetrieverResponseContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LoggingContentRetrieverListener  implements ContentRetrieverListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(LoggingContentRetrieverListener.class);

    @Override
    public void onRequest(ContentRetrieverRequestContext context) {

        context.attributes().put(
                "startNanos",
                System.nanoTime()
        );

        LOG.info(
                "rag.retrieval.start queryLength={}",
                context.query() != null && context.query().text() != null
                        ? context.query().text().length()
                        : 0
        );
    }

    @Override
    public void onResponse(ContentRetrieverResponseContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.info(
                "rag.retrieval.success durationMs={} results={}",
                durationMs,
                context.contents() != null
                        ? context.contents().size()
                        : 0
        );
    }

    @Override
    public void onError(ContentRetrieverErrorContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.error(
                "rag.retrieval.failed durationMs={} exceptionType={} message={}",
                durationMs,
                context.error().getClass().getName(),
                context.error().getMessage(),
                context.error()
        );
    }

}
