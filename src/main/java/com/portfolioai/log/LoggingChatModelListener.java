package com.portfolioai.log;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LoggingChatModelListener implements ChatModelListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(LoggingChatModelListener.class);

    @Override
    public void onRequest(ChatModelRequestContext context) {

        context.attributes().put(
                "startNanos",
                System.nanoTime()
        );

        LOG.info(
                "llm.request.start model={} messages={}",
                context.modelProvider(),
                context.chatRequest().messages().size()
        );
    }

    @Override
    public void onResponse(ChatModelResponseContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        var response = context.chatResponse();

        LOG.info(
                "llm.request.success durationMs={} model={} finishReason={} answerLength={} tokenUsage={}",
                durationMs,
                response.modelName(),
                response.finishReason(),
                response.aiMessage() != null &&
                        response.aiMessage().text() != null
                        ? response.aiMessage().text().length()
                        : 0,
                response.tokenUsage()
        );
    }

    @Override
    public void onError(ChatModelErrorContext context) {

        long startNanos =
                (long) context.attributes().get("startNanos");

        long durationMs =
                (System.nanoTime() - startNanos) / 1_000_000;

        LOG.error(
                "llm.request.failed durationMs={} exceptionType={} message={}",
                durationMs,
                context.error().getClass().getName(),
                context.error().getMessage(),
                context.error()
        );
    }
}