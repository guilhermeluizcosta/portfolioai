package com.portfolioai.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ChatRequest(String question) {

    public static final int MAX_QUESTION_LENGTH = 2000;
}