package com.portfolioai.config;

import dev.langchain4j.store.embedding.filter.Filter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}