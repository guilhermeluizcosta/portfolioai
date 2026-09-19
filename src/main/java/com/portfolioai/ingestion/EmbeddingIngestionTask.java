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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EmbeddingIngestionTask {

    public static void main(String[] args) {
        System.out.println("Iniciando rotina de ingestão de embeddings...");

        Path documentPath = Paths.get("docs");
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(documentPath, new TextDocumentParser());

        DocumentSplitter splitter = DocumentSplitters.recursive(1000, 150);
        List<TextSegment> segments = splitter.splitAll(documents);

        EmbeddingModel embeddingModel = new E5SmallV2QuantizedEmbeddingModel();

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        String outputPath = "target/classes/embeddings.json";
        embeddingStore.serializeToFile(outputPath);

        System.out.println("Ingestão concluída! Arquivo salvo em: " + outputPath);
    }
}
