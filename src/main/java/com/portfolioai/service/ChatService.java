package com.portfolioai.service;

import com.portfolioai.ai.ResumeAssistant;
import com.portfolioai.dto.ChatRequest;
import com.portfolioai.dto.ChatResponse;
import com.portfolioai.exception.ChatProcessingException;
import com.portfolioai.exception.InvalidQuestionException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ChatService {

    private final ResumeAssistant resumeAssistant;
    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ResumeAssistant resumeAssistant) {
        this.resumeAssistant = resumeAssistant;
    }

    public ChatResponse processChat(ChatRequest request) {

        long startNanos = System.nanoTime();
        LOG.info(
                "chat.start questionLength={} thread={}",
                request.question() != null
                        ? request.question().length()
                        : 0,
                Thread.currentThread().getName()
        );

        validateQuestion(request);

        try {
            LOG.info("chat.ai.start");
            long aiStartNanos = System.nanoTime();

            String answer = resumeAssistant.chat(request.question());

            long aiDurationMs = elapsedMs(aiStartNanos);
            long totalDurationMs = elapsedMs(startNanos);


            LOG.info(
                    "chat.ai.success durationMs={} answerLength={}",
                    aiDurationMs,
                    answer != null ? answer.length() : 0
            );

            LOG.info(
                    "chat.success totalDurationMs={}",
                    totalDurationMs
            );

            return new ChatResponse(answer);

        } catch (InvalidQuestionException ex) {

            LOG.warn(
                    "chat.validation.failed durationMs={} reason={}",
                    elapsedMs(startNanos),
                    ex.getMessage()
            );

            throw ex;

        } catch (RuntimeException ex) {

            LOG.error(
                    "chat.failed durationMs={} exceptionType={} message={}",
                    elapsedMs(startNanos),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            throw new ChatProcessingException();
        }
    }

    private void validateQuestion(ChatRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            throw new InvalidQuestionException("Question must not be blank");
        }

        if (request.question().length() > ChatRequest.MAX_QUESTION_LENGTH) {
            throw new InvalidQuestionException("Question exceeds maximum length");
        }

        LOG.debug(
                "chat.validation.success questionLength={}",
                request.question().length()
        );
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}