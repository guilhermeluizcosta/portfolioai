package com.portfolioai.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Serdeable
@Schema(description = "Structured API error")
public record ErrorResponse(
        @Schema(
                description = "Human-readable error message",
                example = "Question must not be blank"
        )
        String error
) {}
