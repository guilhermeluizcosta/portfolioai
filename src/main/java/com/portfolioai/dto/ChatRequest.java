package com.portfolioai.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Serdeable
@Schema(description = "Chat question payload")
public record ChatRequest(
        @Schema(
                description = "Natural-language question about the resume",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = MAX_QUESTION_LENGTH,
                example = "What is your email?"
        )
        String question
) {

    public static final int MAX_QUESTION_LENGTH = 2000;
}