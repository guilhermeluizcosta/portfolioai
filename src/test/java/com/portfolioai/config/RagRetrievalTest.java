package com.portfolioai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RagRetrievalTest {

    private static final int LEGACY_MAX_RESULTS = 10;
    private static final String METADATA_CATEGORY = "category";
    private static final String METADATA_SOURCE_FILE = "source_file";

    private static ContentRetriever contentRetriever;
    private static RagRetrievalProperties properties;

    @BeforeAll
    static void loadRetriever() {
        properties = new RagRetrievalProperties(8, 0.58);
        EmbeddingStore<TextSegment> store = EmbeddingStoreLoader.load(RagRetrievalTest.class.getClassLoader());
        EmbeddingModel model = EmbeddingModelFactory.create();
        contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .maxResults(properties.maxResults())
                .minScore(properties.minScore())
                .dynamicFilter(RagCategoryClassifier::filterForQuery)
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

    @Test
    @DisplayName("contact question retrieves only contacts category chunks")
    void contactQuestionRetrievesOnlyContactsCategory() {
        List<Content> results = contentRetriever.retrieve(Query.from("Qual o e-mail, telefone e LinkedIn para contato?"));

        assertFalse(results.isEmpty());
        boolean onlyContacts = results.stream()
                .map(content -> content.textSegment().metadata().getString(METADATA_CATEGORY))
                .allMatch("contacts"::equals);
        assertTrue(onlyContacts, "contact question must not mix other categories");
    }

    @Test
    @DisplayName("experience question does not rank education chunks in top results")
    void experienceQuestionExcludesEducationFromTopResults() {
        List<Content> results = contentRetriever.retrieve(
                Query.from("Quais empresas você trabalhou e quais cargos ocupou na carreira?"));

        assertFalse(results.isEmpty());
        boolean hasEducation = results.stream()
                .map(content -> content.textSegment().metadata().getString(METADATA_CATEGORY))
                .anyMatch("education"::equals);
        assertFalse(hasEducation, "experience question must not return education chunks");
    }

    @Test
    @DisplayName("cross-category question still returns results from multiple categories")
    void crossCategoryQuestionReturnsMultipleCategories() {
        List<Content> results = contentRetriever.retrieve(
                Query.from("Me dê um resumo do perfil profissional e também os contatos"));

        assertFalse(results.isEmpty());
        Set<String> categories = results.stream()
                .map(content -> content.textSegment().metadata().getString(METADATA_CATEGORY))
                .collect(Collectors.toSet());
        assertTrue(categories.size() > 1, "cross-category question must retrieve from more than one category");
    }

    @Test
    @DisplayName("project links question retrieves all public project source files")
    void projectLinksQuestionRetrievesPublicProjectSources() {
        List<Content> results = contentRetriever.retrieve(Query.from("Quais os links para seus projetos?"));

        Set<String> sourceFiles = sourceFilesFrom(results);
        assertTrue(sourceFiles.contains("xadrez.md"), "expected xadrez.md in top results");
        assertTrue(sourceFiles.contains("airbnb-rio.md"), "expected airbnb-rio.md in top results");
        assertTrue(sourceFiles.contains("forum-comunidade.md"), "expected forum-comunidade.md in top results");
    }

    @Test
    @DisplayName("experience listing question retrieves all experience source files")
    void experienceListingQuestionRetrievesAllExperienceSources() {
        List<Content> results = contentRetriever.retrieve(Query.from("Quais são suas experiências?"));

        Set<String> sourceFiles = sourceFilesFrom(results);
        assertTrue(sourceFiles.contains("banco-inter.md"), "expected banco-inter.md in top results");
        assertTrue(sourceFiles.contains("uni-bh.md"), "expected uni-bh.md in top results");
        assertTrue(sourceFiles.contains("acortinar.md"), "expected acortinar.md in top results");
        assertTrue(sourceFiles.contains("dti-digital.md"), "expected dti-digital.md in top results");
    }

    @Test
    @DisplayName("technology question retrieves profile and work-related categories")
    void technologyQuestionRetrievesProfileAndWorkCategories() {
        List<Content> results = contentRetriever.retrieve(Query.from("Quais tecnologias você domina?"));

        assertFalse(results.isEmpty());
        Set<String> categories = results.stream()
                .map(content -> content.textSegment().metadata().getString(METADATA_CATEGORY))
                .collect(Collectors.toSet());
        assertTrue(categories.contains("profile"), "expected at least one profile segment");
        assertTrue(categories.contains("experience") || categories.contains("projects"),
                "expected at least one experience or projects segment");
    }

    private static Set<String> sourceFilesFrom(List<Content> results) {
        return results.stream()
                .map(content -> content.textSegment().metadata().getString(METADATA_SOURCE_FILE))
                .collect(Collectors.toSet());
    }
}