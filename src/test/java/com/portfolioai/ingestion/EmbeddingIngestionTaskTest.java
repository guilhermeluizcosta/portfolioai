package com.portfolioai.ingestion;

import dev.langchain4j.data.document.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EmbeddingIngestionTask")
class EmbeddingIngestionTaskTest {

    @Nested
    @DisplayName("When docs directory is missing")
    class WhenDocsDirectoryIsMissing {

        @Test
        @DisplayName("Should throw IllegalStateException")
        void shouldThrowWhenDocsDirectoryDoesNotExist(@TempDir Path tempDir) {
            Path missingDocs = tempDir.resolve("missing-docs");

            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> EmbeddingIngestionTask.loadAndEnrichMarkdownDocuments(missingDocs));

            assertTrue(error.getMessage().contains("ERRO: O Java não está conseguindo enxergar a pasta:"));
        }
    }

    @Nested
    @DisplayName("When markdown files exist")
    class WhenMarkdownFilesExist {

        @Test
        @DisplayName("Should enrich documents with category and source_file metadata")
        void shouldEnrichDocumentsWithMetadata(@TempDir Path tempDir) throws Exception {
            Path categoryDir = tempDir.resolve("experiencia");
            Files.createDirectories(categoryDir);
            Path markdownFile = categoryDir.resolve("projeto-x.md");
            Files.writeString(markdownFile, "Built a payment service with Java.");

            List<Document> documents = EmbeddingIngestionTask.loadAndEnrichMarkdownDocuments(tempDir);

            assertEquals(1, documents.size());
            Document document = documents.getFirst();
            assertEquals("experiencia", document.metadata().getString("category"));
            assertEquals("projeto-x.md", document.metadata().getString("source_file"));
            assertTrue(document.text().contains("[CONTEXTO - Categoria: EXPERIENCIA | Arquivo: projeto-x.md]"));
            assertTrue(document.text().contains("Built a payment service with Java."));
        }
    }

    @Nested
    @DisplayName("When docs directory has no markdown files")
    class WhenDocsDirectoryHasNoMarkdownFiles {

        @Test
        @DisplayName("Should throw IllegalStateException")
        void shouldThrowWhenNoMarkdownFilesFound(@TempDir Path tempDir) throws Exception {
            Files.createDirectories(tempDir.resolve("empty-category"));

            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> EmbeddingIngestionTask.loadAndEnrichMarkdownDocuments(tempDir));

            assertTrue(error.getMessage().contains("nenhum arquivo .md"));
        }
    }
}