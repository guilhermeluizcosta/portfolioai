package com.portfolioai.config;

import dev.langchain4j.store.embedding.filter.Filter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RagCategoryClassifierTest {

    @Test
    @DisplayName("contact question resolves to contacts filter")
    void contactQuestionResolvesToContactsFilter() {
        Set<String> categories = RagCategoryClassifier.detectCategories("Qual o e-mail e telefone para contato?");

        assertEquals(Set.of("contacts"), categories);
        assertNotNull(RagCategoryClassifier.filterForQuestion("Qual o e-mail e telefone para contato?"));
    }

    @Test
    @DisplayName("experience question resolves to experience filter")
    void experienceQuestionResolvesToExperienceFilter() {
        Set<String> categories = RagCategoryClassifier.detectCategories(
                "Quais empresas e cargos na sua carreira profissional?");

        assertEquals(Set.of("experience"), categories);
        assertNotNull(RagCategoryClassifier.filterForQuestion("Quais empresas e cargos na sua carreira profissional?"));
    }

    @Test
    @DisplayName("cross-category question skips filter")
    void crossCategoryQuestionSkipsFilter() {
        Filter filter = RagCategoryClassifier.filterForQuestion("Me dê um resumo do perfil e também os contatos");

        assertNull(filter);
    }

    @Test
    @DisplayName("project links question resolves to projects-only filter")
    void projectLinksQuestionResolvesToProjectsFilter() {
        Set<String> categories = RagCategoryClassifier.detectCategories("Quais os links para seus projetos?");

        assertEquals(Set.of("projects"), categories);
        assertNotNull(RagCategoryClassifier.filterForQuestion("Quais os links para seus projetos?"));
    }

    @Test
    @DisplayName("technology question spans multiple categories and skips filter")
    void technologyQuestionSpansMultipleCategories() {
        Set<String> categories = RagCategoryClassifier.detectCategories("Quais tecnologias você domina?");

        assertTrue(categories.containsAll(Set.of("profile", "experience", "projects")));
        assertNull(RagCategoryClassifier.filterForQuestion("Quais tecnologias você domina?"));
    }

    @Test
    @DisplayName("experience listing question resolves to experience filter")
    void experienceListingQuestionResolvesToExperienceFilter() {
        Set<String> categories = RagCategoryClassifier.detectCategories("Quais são suas experiências?");

        assertEquals(Set.of("experience"), categories);
        assertNotNull(RagCategoryClassifier.filterForQuestion("Quais são suas experiências?"));
    }
}