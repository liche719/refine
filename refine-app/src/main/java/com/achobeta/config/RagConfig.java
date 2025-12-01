package com.achobeta.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@EnableConfigurationProperties(RagConfigProperties.class)
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Resource
    private RagConfigProperties ragConfigProperties;

    private EmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    public void initialize() {
        try {
            log.info("开始初始化Weaviate EmbeddingStore");
            
            // 暂时跳过EmbeddingStore初始化，因为我们使用自定义的向量存储
            log.info("RagConfig初始化完成，使用自定义向量存储服务");
            
        } catch (Exception e) {
            log.error("RagConfig初始化失败", e);
        }
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 返回null，因为我们使用自定义的向量存储服务
        log.info("使用自定义向量存储服务，不创建EmbeddingStore Bean");
        return null;
    }

    @Bean
    public ContentRetriever contentRetriever() {
        // RAG
        // 加载文档
        String docPath = "refine-app/src/main/resources/docs";
        File docDir = new File(docPath);

        // 检查目录是否存在以及是否有文件
        if (docDir.exists() && docDir.isDirectory()) {
            // 获取目录下的文件（排除子目录，只考虑文件）
            File[] files = docDir.listFiles(File::isFile);
            if (files != null && files.length > 0) {
                // TODO: 使用Weaviate实现文档向量化和存储
                log.warn("RagConfig暂未实现Weaviate文档向量化，跳过文档加载");
            } else {
                log.info("文档目录 {} 下无可用文件，跳过文档加载（表中数据保持不变）", docPath);
            }
        } else {
            log.warn("文档目录 {} 不存在或不是目录，跳过文档加载", docPath);
        }

        // 自定义内容加载器
        return new ContentRetriever() {
            @Override
            public List<Content> retrieve(Query query) {
                try {
                    log.info("ContentRetriever收到查询请求: {}", query.text());
                    
                    // 暂时返回空结果，因为我们主要关注学习行为记录
                    // 后续可以集成Weaviate进行内容检索
                    log.info("ContentRetriever暂时返回空结果，专注于学习行为记录功能");
                    return List.of();
                    
                } catch (Exception e) {
                    log.error("ContentRetriever查询失败", e);
                    return List.of();
                }
            }
        };
    }

    // 注释掉PgVector相关的表操作方法
    // private void truncateEmbeddingTable() {
    //     // TODO: 使用Weaviate实现数据清理
    //     log.warn("RagConfig暂未实现Weaviate数据清理");
    // }

    // private void deleteAllEmbeddingData() {
    //     // TODO: 使用Weaviate实现数据删除
    //     log.warn("RagConfig暂未实现Weaviate数据删除");
    // }
}