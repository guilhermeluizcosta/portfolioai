package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmbeddingStoreLoaderTest {

    @Test
    @DisplayName("loads embedding store from classpath stream without jar filesystem path")
    void loadsFromClasspathStream() {
        EmbeddingStore<TextSegment> store = EmbeddingStoreLoader.load(
                EmbeddingStoreLoaderTest.class.getClassLoader());

        assertNotNull(store);
    }
}