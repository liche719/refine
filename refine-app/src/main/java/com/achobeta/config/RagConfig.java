package com.achobeta.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

@Configuration
@Slf4j
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    private EmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    public void initialize() {
        this.embeddingStore = PgVectorEmbeddingStore.builder()
                .host("156.225.19.144")
                .port(15432)
                .database("postgres")
                .user("root")
                .password("123456")
                .table("knowledge_embeddings")
                .dimension(qwenEmbeddingModel.dimension())
                .build();
        log.info("EmbeddingStore初始化完成");
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return this.embeddingStore;
    }

    @Bean
    public ContentRetriever contentRetriever() {
        // RAG
        // 加载文档     TODO:不加载没有变动的文档
        String docPath = "refine-app/src/main/resources/docs";
        File docDir = new File(docPath);

        // 检查目录是否存在以及是否有文件
        if (docDir.exists() && docDir.isDirectory()) {
            // 获取目录下的文件（排除子目录，只考虑文件）
            File[] files = docDir.listFiles(File::isFile);
            if (files != null && files.length > 0) {
                List<Document> documents = FileSystemDocumentLoader.loadDocuments(docPath);
                // 文档分段
                DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(800, 350);
                // 自定义文档加载器，把文档转换成向量并存储到向量数据库中
                EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                        .documentSplitter(documentSplitter)
                        .textSegmentTransformer(textSegment -> TextSegment.from(
                                textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                                textSegment.metadata()
                        ))
                        .embeddingModel(qwenEmbeddingModel)
                        .embeddingStore(embeddingStore)
                        .build();
                // 加载文档
                ingestor.ingest(documents);
                log.info("文档初始化入库完成，共加载 {} 个文件", files.length);
            } else {
                log.info("文档目录 {} 下无可用文件，跳过文档加载", docPath);
            }
        } else {
            log.warn("文档目录 {} 不存在或不是目录，跳过文档加载", docPath);
        }

        // 自定义内容加载器
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5) // 最多返回5条结果
                .minScore(0.75) // 过滤掉分数小于0.75的结果
                .build();
        return retriever;
    }
}