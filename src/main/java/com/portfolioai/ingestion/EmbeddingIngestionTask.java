package com.portfolioai.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.e5smallv2q.E5SmallV2QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EmbeddingIngestionTask {

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando rotina de ingestão de embeddings com metadados...");
        ingest(Paths.get("docs"), Paths.get("target/classes/embeddings.json"));
    }

    static void ingest(Path documentPath, Path outputPath) throws Exception {
        System.out.println("-> Buscando documentos na pasta: " + documentPath.toAbsolutePath());

        List<Document> enrichedDocuments = loadAndEnrichMarkdownDocuments(documentPath);

        System.out.println("-> Total de arquivos .md processados e enriquecidos: " + enrichedDocuments.size());

        DocumentSplitter splitter = DocumentSplitters.recursive(3000, 250);
        List<TextSegment> segments = splitter.splitAll(enrichedDocuments);

        EmbeddingModel embeddingModel = new E5SmallV2QuantizedEmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        embeddingStore.serializeToFile(outputPath.toString());

        System.out.println("Ingestão concluída! Arquivo salvo em: " + outputPath);
    }

    static List<Document> loadAndEnrichMarkdownDocuments(Path documentPath) throws Exception {
        if (!Files.exists(documentPath)) {
            throw new IllegalStateException("ERRO: O Java não está conseguindo enxergar a pasta: " + documentPath.toAbsolutePath());
        }

        List<Document> enrichedDocuments = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(documentPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".md"))
                    .forEach(filePath -> {
                        System.out.println("-> Lendo arquivo: " + filePath.toString());

                        Document doc = FileSystemDocumentLoader.loadDocument(filePath, new TextDocumentParser());

                        String fileName = filePath.getFileName().toString();
                        String category = filePath.getParent().getFileName().toString();

                        doc.metadata().put("category", category);
                        doc.metadata().put("source_file", fileName);

                        String header = String.format("[CONTEXTO - Categoria: %s | Arquivo: %s]\n\n", category.toUpperCase(), fileName);
                        String enrichedText = header + doc.text();

                        enrichedDocuments.add(Document.from(enrichedText, doc.metadata()));
                    });
        }

        if (enrichedDocuments.isEmpty()) {
            throw new IllegalStateException("ERRO: A pasta existe, mas a varredura não encontrou nenhum arquivo .md dentro das subpastas.");
        }

        return enrichedDocuments;
    }
}