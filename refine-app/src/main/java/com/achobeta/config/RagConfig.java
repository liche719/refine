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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        this.embeddingStore = PgVectorEmbeddingStore.builder()
                .host(ragConfigProperties.getHost())
                .port(ragConfigProperties.getPort())
                .database(ragConfigProperties.getDatabase())
                .user(ragConfigProperties.getUser())
                .password(ragConfigProperties.getPassword())
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
                // 提取所有待加载的文件名（从Document的metadata中获取）
                List<String> fileNames = documents.stream()
                        .map(doc -> doc.metadata().getString("file_name")) // FileSystemDocumentLoader自动添加file_name元数据
                        .distinct() // 去重（避免同一文件多次删除）
                        .collect(Collectors.toList());
                log.info("待加载的文件列表：{}", fileNames);
                if (!fileNames.isEmpty()) {
                    deleteExistingDataByFileNames(fileNames);
                }

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

    /**
     * 核心方法：删除数据库中指定文件名对应的所有旧数据
     * 利用PostgreSQL JSONB语法匹配metadata中的file_name字段
     */
    private void deleteExistingDataByFileNames(List<String> fileNames) {
        // SQL：删除metadata->>'file_name'在指定列表中的数据（JSONB字段取值）
        String sql = String.format(
                "DELETE FROM %s WHERE metadata->>'file_name' = ANY (?)",
                "knowledge_embeddings" // 表名
        );

        try (
                // 1. 获取数据库连接（复用pgvector配置）
                Connection conn = DriverManager.getConnection(
                        String.format("jdbc:postgresql://%s:%d/%s", "156.225.19.144", 15432, "postgres"),
                        "root",
                        "123456"
                );
                // 2. 预处理SQL（避免SQL注入）
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // 3. 设置参数：文件名列表（PostgreSQL的ANY接收数组参数）
            pstmt.setArray(1, conn.createArrayOf("varchar", fileNames.toArray()));

            // 4. 执行删除，返回删除行数
            int deletedRows = pstmt.executeUpdate();
            log.info("删除数据库中已存在的旧数据成功，共删除 {} 条记录（涉及文件：{}）", deletedRows, fileNames);

        } catch (SQLException e) {
            log.error("删除旧数据失败（文件列表：{}）", fileNames, e);
            throw new RuntimeException("删除旧数据异常，终止文档入库", e); // 中断流程，避免重复数据
        }
    }
}