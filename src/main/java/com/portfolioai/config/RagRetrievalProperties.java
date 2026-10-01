package com.portfolioai.config;

import io.micronaut.context.annotation.ConfigurationInject;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;

import java.util.Objects;

@ConfigurationProperties("portfolioai.rag")
public record RagRetrievalProperties(
        @Bindable(defaultValue = "4") Integer maxResults,
        @Bindable(defaultValue = "0.65") Double minScore
) {

    @ConfigurationInject
    public RagRetrievalProperties {
        Objects.requireNonNull(maxResults, "maxResults");
        Objects.requireNonNull(minScore, "minScore");
    }
}
