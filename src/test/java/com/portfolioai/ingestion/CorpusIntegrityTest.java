package com.portfolioai.ingestion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Corpus integrity")
class CorpusIntegrityTest {

    private static final Path DOCS_ROOT = Paths.get("docs");
    private static final Path PROJECTS_DIR = DOCS_ROOT.resolve("projects");
    private static final Path EXPERIENCE_DIR = DOCS_ROOT.resolve("experience");
    private static final Pattern H1_PATTERN = Pattern.compile("^# (.+)$", Pattern.MULTILINE);
    private static final Pattern LINK_PUBLICO_PATTERN = Pattern.compile("^\\*\\*Link público:\\*\\* (.+)$", Pattern.MULTILINE);
    private static final Set<String> ALLOWED_LINK_PUBLICO_VALUES = Set.of("Sim", "Não");

    @Nested
    @DisplayName("Project markdown files")
    class ProjectMarkdownFiles {

        @Test
        @DisplayName("Should have unique H1 titles across all project files")
        void shouldHaveUniqueH1Titles() throws IOException {
            Map<String, String> fileByH1 = new HashMap<>();
            List<Path> projectFiles = listProjectMarkdownFiles();

            for (Path file : projectFiles) {
                String h1 = extractH1(readMarkdown(file));
                assertNotNull(h1, "Missing H1 in " + file.getFileName());

                String previousFile = fileByH1.put(normalize(h1), file.getFileName().toString());
                if (previousFile != null) {
                    fail("Duplicate H1 '" + h1 + "' in " + previousFile + " and " + file.getFileName());
                }
            }

            assertFalse(projectFiles.isEmpty(), "Expected at least one project markdown file");
        }

        @Test
        @DisplayName("Should declare Link público in every project file")
        void shouldDeclareLinkPublicoInEveryProjectFile() throws IOException {
            List<Path> projectFiles = listProjectMarkdownFiles();
            assertFalse(projectFiles.isEmpty(), "Expected at least one project markdown file");

            for (Path file : projectFiles) {
                assertLinkPublicoDeclared(file);
            }
        }

        @Test
        @DisplayName("Should match canonical H1 keywords for known project filenames")
        void shouldMatchCanonicalH1KeywordsForKnownProjectFilenames() throws IOException {
            assertH1ContainsKeyword(PROJECTS_DIR.resolve("forum-comunidade.md"), "forum de comunidade");
            assertH1ContainsKeyword(PROJECTS_DIR.resolve("airbnb-rio.md"), "airbnb rio");
        }
    }

    @Nested
    @DisplayName("Experience markdown files")
    class ExperienceMarkdownFiles {

        @Test
        @DisplayName("Should match canonical H1 keywords for known experience filenames")
        void shouldMatchCanonicalH1KeywordsForKnownExperienceFilenames() throws IOException {
            assertH1ContainsKeyword(EXPERIENCE_DIR.resolve("acortinar.md"), "acortinar");
            assertH1ContainsKeyword(EXPERIENCE_DIR.resolve("uni-bh.md"), "unibh");
        }
    }

    private static List<Path> listProjectMarkdownFiles() throws IOException {
        try (var paths = Files.list(PROJECTS_DIR)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .toList();
        }
    }

    private static String readMarkdown(Path file) throws IOException {
        return Files.readString(file);
    }

    private static void assertLinkPublicoDeclared(Path file) throws IOException {
        Matcher matcher = LINK_PUBLICO_PATTERN.matcher(readMarkdown(file));
        assertTrue(
                matcher.find(),
                file.getFileName() + " must contain **Link público:** Sim or Não");
        String value = matcher.group(1).trim();
        assertTrue(
                ALLOWED_LINK_PUBLICO_VALUES.contains(value),
                file.getFileName() + " must use Sim or Não for Link público, got: " + value);
    }

    private static void assertH1ContainsKeyword(Path file, String expectedKeyword) throws IOException {
        String h1 = extractH1(readMarkdown(file));
        assertNotNull(h1, "Missing H1 in " + file.getFileName());
        assertTrue(
                normalize(h1).contains(expectedKeyword),
                file.getFileName() + " H1 must contain '" + expectedKeyword + "', got: " + h1);
    }

    private static String extractH1(String content) {
        Matcher matcher = H1_PATTERN.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).trim();
    }

    private static String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }
}