package com.portfolioai.config;

import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG =
            LoggerFactory.getLogger(RagCategoryClassifier.class);

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
            LOG.debug("rag.filter.skip reason=blank_question");
            return null;
        }

        String normalized = normalize(question);

        if (isProjectLinksQuestion(normalized)) {
            LOG.debug("rag.filter.category=projects reason=project_links");
            return metadataKey("category").isEqualTo(CATEGORY_PROJECTS);
        }

        Set<String> categories = detectCategories(question);

        if (categories.size() != 1) {
            LOG.debug(
                    "rag.filter.none categories={} reason=ambiguous_category",
                    categories
            );
            return null;
        }

        String category = categories.iterator().next();

        if (CATEGORY_EXPERIENCE.equals(category)) {

            if (isBroadCareerQuestion(normalized)) {

                LOG.debug(
                        "rag.filter.categories=[experience,projects] reason=broad_career_question"
                );

                return metadataKey("category")
                        .isIn(CATEGORY_EXPERIENCE, CATEGORY_PROJECTS);
            }

            LOG.debug("rag.filter.category=experience");

            return metadataKey("category")
                    .isEqualTo(CATEGORY_EXPERIENCE);
        }

        LOG.debug("rag.filter.category={}", category);

        return metadataKey("category")
                .isEqualTo(category);
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
        if (matchesAny(normalized, "tecnologia", "tecnologias", "stack", "domina", "skills")) {
            categories.add(CATEGORY_PROFILE);
            categories.add(CATEGORY_EXPERIENCE);
            categories.add(CATEGORY_PROJECTS);
        }
        if (matchesAny(normalized, "experiencia", "emprego", "empresa", "cargo", "trabalho", "carreira", "responsabilidade")) {
            categories.add(CATEGORY_EXPERIENCE);
        }
        if (isProjectLinksQuestion(normalized) || matchesAny(normalized, "projeto", "projetos", "repositorio", "github.com")) {
            categories.add(CATEGORY_PROJECTS);
        }
        return categories;
    }

    private static boolean isProjectLinksQuestion(String normalized) {
        return matchesAny(normalized, "link", "url", "github", "repositorio")
                && matchesAny(normalized, "projeto", "projetos");
    }

    private static boolean isBroadCareerQuestion(String normalized) {
        return matchesAny(normalized, "carreira", "emprego", "empresa", "cargo", "trabalho", "responsabilidade");
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