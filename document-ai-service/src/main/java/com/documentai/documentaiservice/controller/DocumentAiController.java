package com.documentai.documentaiservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocumentAiController {

    private final SimpleVectorStore vectorStore;
    private final ChatClient chatClient;

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        List<Document> documents = reader.read();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true)
                .build();

        List<Document> chunks = splitter.split(documents);
        vectorStore.add(chunks);
        return "Indexed successfully! Total chunks: " + chunks.size();
    }

    @GetMapping("/query")
    public String queryDocument(@RequestParam("question") String question) {
        List<Document> matchingDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(3).build()
        );

        String referenceContext = matchingDocs.stream()
                .map(Document::getText).collect(Collectors.joining("\n"));

        String promptInstruction = """
                Answer the user's question using the provided context.
                If you don't know the answer, say 'Information not found.'
                
                CONTEXT:
                %s
                """.formatted(referenceContext);
        return chatClient.prompt()
                .system(promptInstruction)
                .user(question)
                .call().content();
    }

}
