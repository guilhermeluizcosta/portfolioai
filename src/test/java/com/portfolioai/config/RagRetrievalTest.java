package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.e5smallv2q.E5SmallV2QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RagRetrievalTest {

    private static final int LEGACY_MAX_RESULTS = 10;

    private static ContentRetriever contentRetriever;
    private static RagRetrievalProperties properties;

    @BeforeAll
    static void loadRetriever() throws Exception {
        URL embeddingsUrl = RagRetrievalTest.class.getClassLoader().getResource("embeddings.json");
        if (embeddingsUrl == null) {
            throw new IllegalStateException("embeddings.json not found — run mvn compile first");
        }

        properties = new RagRetrievalProperties(4, 0.65);
        EmbeddingStore<TextSegment> store = InMemoryEmbeddingStore.fromFile(Paths.get(embeddingsUrl.toURI()));
        EmbeddingModel model = new E5SmallV2QuantizedEmbeddingModel();
        contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .maxResults(properties.maxResults())
                .minScore(properties.minScore())
                .build();
    }

    @Test
    @DisplayName("contact question returns bounded segments, not full corpus dump")
    void contactQuestionReturnsBoundedSegments() {
        List<Content> results = contentRetriever.retrieve(Query.from("What is the email and phone contact information?"));

        assertFalse(results.isEmpty(), "expected at least one contact-related segment");
        assertTrue(results.size() < LEGACY_MAX_RESULTS, "retrieval must return fewer segments than legacy maxResults(10)");
        assertTrue(results.size() <= properties.maxResults(), "retrieval must not exceed configured maxResults");
    }

    @Test
    @DisplayName("contact question retrieves segment with contact details")
    void contactQuestionRetrievesRelevantSegment() {
        List<Content> results = contentRetriever.retrieve(Query.from("LinkedIn profile and email address"));

        boolean hasContactContent = results.stream()
                .map(content -> content.textSegment().text().toLowerCase())
                .anyMatch(text -> text.contains("linkedin") || text.contains("guilhermelc10@gmail.com"));

        assertTrue(hasContactContent, "top results should include contact information");
    }
}