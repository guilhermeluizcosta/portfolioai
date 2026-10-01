package com.portfolioai.config;

import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.filter.Filter;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

public final class RagCategoryClassifier {

    private static final String CATEGORY_CONTACTS = "contacts";
    private static final String CATEGORY_EDUCATION = "education";
    private static final String CATEGORY_EXPERIENCE = "experience";
    private static final String CATEGORY_PROFILE = "profile";
    private static final String CATEGORY_PROJECTS = "projects";

    private RagCategoryClassifier() {
    }

    public static Filter filterForQuery(Query query) {
        if (query == null || query.text() == null || query.text().isBlank()) {
            return null;
        }
        return filterForQuestion(query.text());
    }

    public static Filter filterForQuestion(String question) {
        if (question == null || question.isBlank()) {
            return null;
        }
        Set<String> categories = detectCategories(question);
        if (categories.size() != 1) {
            return null;
        }
        String category = categories.iterator().next();
        if (CATEGORY_EXPERIENCE.equals(category)) {
            return metadataKey("category").isIn(CATEGORY_EXPERIENCE, CATEGORY_PROJECTS);
        }
        return metadataKey("category").isEqualTo(category);
    }

    static Set<String> detectCategories(String question) {
        String normalized = normalize(question);
        Set<String> categories = new LinkedHashSet<>();
        if (matchesAny(normalized, "contato", "contatos", "email", "e-mail", "telefone", "whatsapp", "linkedin", "instagram")) {
            categories.add(CATEGORY_CONTACTS);
        }
        if (matchesAny(normalized, "educacao", "graduacao", "curso", "certificacao", "faculdade", "universidade", "mba", "pos-graduacao")) {
            categories.add(CATEGORY_EDUCATION);
        }
        if (matchesAny(normalized, "perfil", "resumo", "objetivo", "apresentacao", "sobre mim", "idioma", "localizacao", "caracteristica")) {
            categories.add(CATEGORY_PROFILE);
        }
        if (matchesAny(normalized, "experiencia", "emprego", "empresa", "cargo", "trabalho", "carreira", "responsabilidade")) {
            categories.add(CATEGORY_EXPERIENCE);
        }
        if (matchesAny(normalized, "projeto", "projetos", "repositorio", "github.com")) {
            categories.add(CATEGORY_EXPERIENCE);
        }
        return categories;
    }

    private static boolean matchesAny(String normalized, String... terms) {
        for (String term : terms) {
            if (normalized.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        return Normalizer.normalize(lower, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
