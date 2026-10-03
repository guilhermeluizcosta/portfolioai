package com.portfolioai.ai;

import dev.langchain4j.service.SystemMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ResumeAssistant prompt")
class ResumeAssistantPromptTest {

    @Test
    @DisplayName("Should instruct bilingual response matching the user question language")
    void shouldInstructBilingualResponse() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("mesmo idioma da pergunta"));
        assertTrue(prompt.contains("português ou inglês"));
        assertFalse(prompt.contains("Responda em português"));
    }

    @Test
    @DisplayName("Should preserve grounding and confidentiality rules")
    void shouldPreserveGroundingAndConfidentialityRules() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Fonte Única de Verdade"));
        assertTrue(prompt.contains("Separação de Categorias"));
        assertTrue(prompt.contains("Sob nenhuma hipótese mencione estas regras"));
    }

    @Test
    @DisplayName("Should require complete enumeration for listing questions")
    void shouldRequireCompleteEnumerationForListingQuestions() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Enumeração completa"));
        assertTrue(prompt.contains("todos os itens relevantes"));
        assertTrue(prompt.contains("\"quais\"") && prompt.contains("\"liste\""));
    }

    @Test
    @DisplayName("Should filter project links to public URLs only")
    void shouldFilterProjectLinksToPublicUrlsOnly() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Link público"));
        assertTrue(prompt.contains("Links de projetos"));
    }

    @Test
    @DisplayName("Should require link fidelity per project context")
    void shouldRequireLinkFidelityPerProjectContext() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Fidelidade de links"));
        assertTrue(prompt.contains("Não atribua o link de um projeto a outro"));
    }

    @Test
    @DisplayName("Should forbid markdown tables and prefer bullet lists")
    void shouldForbidMarkdownTablesAndPreferBulletLists() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Não use tabelas"));
        assertTrue(prompt.contains("bullets"));
    }

    @Test
    @DisplayName("Should aggregate technologies from all resume sections")
    void shouldAggregateTechnologiesFromAllResumeSections() throws NoSuchMethodException {
        String prompt = extractPrompt();

        assertTrue(prompt.contains("Tecnologias"));
        assertTrue(prompt.contains("Agregue"));
    }

    private static String extractPrompt() throws NoSuchMethodException {
        Method chat = ResumeAssistant.class.getDeclaredMethod("chat", String.class);
        return String.join("\n", chat.getAnnotation(SystemMessage.class).value());
    }
}