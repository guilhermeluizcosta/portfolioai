package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class EmbeddingStoreLoader {

    private EmbeddingStoreLoader() {
    }

    public static EmbeddingStore<TextSegment> load(ClassLoader classLoader) {
        try (InputStream inputStream = classLoader.getResourceAsStream("embeddings.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("Arquivo embeddings.json não encontrado.");
            }

            Path tempFile = Files.createTempFile("embeddings", ".json");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            return InMemoryEmbeddingStore.fromFile(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar o banco de vetores local", e);
        }
    }
}