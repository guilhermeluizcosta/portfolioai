package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class EmbeddingStoreLoader {

    private static final Logger LOG =
            LoggerFactory.getLogger(EmbeddingStoreLoader.class);

    private EmbeddingStoreLoader() {
    }

    public static EmbeddingStore<TextSegment> load(ClassLoader classLoader) {

        long startNanos = System.nanoTime();

        try (InputStream inputStream =
                     classLoader.getResourceAsStream("embeddings.json")) {

            if (inputStream == null) {
                LOG.error("embedding.store.load.failed file_not_found");
                throw new IllegalStateException(
                        "Arquivo embeddings.json não encontrado."
                );
            }

            Path tempFile =
                    Files.createTempFile("embeddings", ".json");

            Files.copy(
                    inputStream,
                    tempFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

            tempFile.toFile().deleteOnExit();

            EmbeddingStore<TextSegment> store =
                    InMemoryEmbeddingStore.fromFile(tempFile);

            long durationMs =
                    (System.nanoTime() - startNanos) / 1_000_000;

            LOG.info(
                    "embedding.store.loaded durationMs={} fileSizeBytes={} implementation={}",
                    durationMs,
                    Files.size(tempFile),
                    store.getClass().getSimpleName()
            );

            return store;

        } catch (IOException e) {

            LOG.error(
                    "embedding.store.load.failed exceptionType={} message={}",
                    e.getClass().getName(),
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "Falha ao carregar o banco de vetores local",
                    e
            );
        }
    }
}