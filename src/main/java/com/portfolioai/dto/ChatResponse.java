package com.portfolioai.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Serdeable
@Schema(description = "Successful chat answer")
public record ChatResponse(
        @Schema(
                description = "Grounded answer from the resume assistant",
                example = "guilhermelc10@gmail.com"
        )
        String answer
) {}