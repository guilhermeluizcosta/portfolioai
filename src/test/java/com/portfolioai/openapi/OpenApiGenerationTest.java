package com.portfolioai.openapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OpenAPI generation")
class OpenApiGenerationTest {

    @Test
    @DisplayName("compile generates OpenAPI YAML under META-INF/swagger")
    void generatesOpenApiYamlAfterCompile() throws IOException {
        Path swaggerDir = Path.of("target/classes/META-INF/swagger");

        assertTrue(Files.isDirectory(swaggerDir), "META-INF/swagger directory must exist after compile");

        try (Stream<Path> files = Files.list(swaggerDir)) {
            assertTrue(files.anyMatch(OpenApiGenerationTest::isYamlSpec), "OpenAPI spec YAML must be generated at compile time");
        }
    }

    @Test
    @DisplayName("generated spec documents POST /api/v1/chat")
    void documentsChatEndpoint() throws IOException {
        Path spec = findGeneratedSpec();

        String content = Files.readString(spec);

        assertTrue(content.contains("/api/v1/chat"), "spec must include chat path");
        assertTrue(content.contains("ChatRequest"), "spec must include ChatRequest schema");
        assertTrue(content.contains("ChatResponse"), "spec must include ChatResponse schema");
        assertTrue(content.contains("ErrorResponse"), "spec must include ErrorResponse schema");
    }

    private static Path findGeneratedSpec() throws IOException {
        Path swaggerDir = Path.of("target/classes/META-INF/swagger");

        try (Stream<Path> files = Files.list(swaggerDir)) {
            return files
                    .filter(OpenApiGenerationTest::isYamlSpec)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("no OpenAPI YAML found"));
        }
    }

    private static boolean isYamlSpec(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith(".yml") || name.endsWith(".yaml");
    }
}