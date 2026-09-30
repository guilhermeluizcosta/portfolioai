package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import java.util.Map;

@Singleton
public class EmbeddingStoreHealthIndicator implements HealthIndicator {

    private final EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingStoreHealthIndicator(EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingStore = embeddingStore;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return Publishers.just(
                HealthResult.builder("embeddingStore", HealthStatus.UP)
                        .details(Map.of("implementation", embeddingStore.getClass().getSimpleName()))
                        .build());
    }
}
