package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.rag.model.valobj.LearningInsightVO;
import com.achobeta.domain.rag.model.valobj.SimilarQuestionVO;
import com.achobeta.domain.rag.service.IVectorService;
import com.achobeta.infrastructure.gateway.DashScopeEmbeddingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auth : Malog
 * @Desc : Weaviate向量数据库适配器实现
 * @Time : 2025/11/28
 */
@Slf4j
@Repository
@Qualifier("weaviateVectorRepository")
public class WeaviateVectorRepository implements IVectorService {

    @Autowired
    private WeaviateClient weaviateClient;

    @Autowired
    private String weaviateClassName;

    @Autowired
    private DashScopeEmbeddingService embeddingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 初始化Weaviate Schema
     */
    @PostConstruct
    public void initializeSchema() {
        try {
            // 检查类是否已存在
            Result<WeaviateClass> existsResult = weaviateClient.schema().classGetter().withClassName(weaviateClassName).run();
            
            if (existsResult.hasErrors()) {
                // 如果获取失败，可能是类不存在，尝试创建
                log.info("Weaviate类 {} 不存在，开始创建", weaviateClassName);
                createWeaviateClass();
                log.info("Weaviate类 {} 创建成功", weaviateClassName);
            } else {
                log.info("Weaviate类 {} 已存在", weaviateClassName);
            }
        } catch (Exception e) {
            log.error("初始化Weaviate Schema失败", e);
        }
    }

    /**
     * 创建Weaviate类定义
     */
    private void createWeaviateClass() {
        WeaviateClass weaviateClass = WeaviateClass.builder()
                .className(weaviateClassName)
                .description("学习向量数据存储")
                .vectorizer("none") // 使用自定义向量
                .properties(Arrays.asList(
                        Property.builder()
                                .name("userId")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("用户ID")
                                .build(),
                        Property.builder()
                                .name("questionId")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("题目ID")
                                .build(),
                        Property.builder()
                                .name("actionType")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("操作类型")
                                .build(),
                        Property.builder()
                                .name("questionContent")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("题目内容")
                                .build(),
                        Property.builder()
                                .name("subject")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("科目")
                                .build(),
                        Property.builder()
                                .name("knowledgePointId")
                                .dataType(Arrays.asList(DataType.INT))
                                .description("知识点ID")
                                .build(),
                        Property.builder()
                                .name("metadata")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("元数据JSON")
                                .build(),
                        Property.builder()
                                .name("createdAt")
                                .dataType(Arrays.asList(DataType.TEXT))
                                .description("创建时间")
                                .build()
                ))
                .build();

        Result<Boolean> result = weaviateClient.schema().classCreator().withClass(weaviateClass).run();
        if (result.hasErrors()) {
            throw new RuntimeException("创建Weaviate类失败: " + result.getError().getMessages());
        }
    }

    @Override
    public boolean storeLearningVector(String userId, String questionId, String questionContent,
                                       String actionType, String subject, Integer knowledgePointId) {
        try {
            // 构建向量化文本
            String vectorText = buildVectorText(actionType, questionContent, subject);

            // 生成向量
            float[] embedding = embeddingService.generateEmbedding(vectorText);
            
            // 转换为Float[]数组
            Float[] embeddingArray = new Float[embedding.length];
            for (int i = 0; i < embedding.length; i++) {
                embeddingArray[i] = embedding[i];
            }

            // 构建元数据
            Map<String, Object> metadata = buildMetadata(actionType, subject, knowledgePointId);
            String metadataJson = objectMapper.writeValueAsString(metadata);

            // 构建Weaviate对象
            Map<String, Object> properties = new HashMap<>();
            properties.put("userId", userId);
            properties.put("questionId", questionId);
            properties.put("actionType", actionType);
            properties.put("questionContent", questionContent);
            properties.put("subject", subject);
            properties.put("knowledgePointId", knowledgePointId);
            properties.put("metadata", metadataJson);
            properties.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // 存储到Weaviate
            Result<WeaviateObject> result = weaviateClient.data().creator()
                    .withClassName(weaviateClassName)
                    .withProperties(properties)
                    .withVector(embeddingArray)
                    .run();

            if (result.hasErrors()) {
                log.error("=== Weaviate存储错误 ===");
                log.error("错误信息: {}", result.getError().getMessages());
                log.error("用户ID: {}", userId);
                log.error("题目ID: {}", questionId);
                log.error("======================");
                return false;
            }

            log.info("=== 学习向量存储成功 ===");
            log.info("用户ID: {}", userId);
            log.info("题目ID: {}", questionId);
            log.info("行为类型: {}", actionType);
            log.info("科目: {}", subject != null ? subject : "未知");
            log.info("题目内容: {}", questionContent != null ? 
                    (questionContent.length() > 100 ? questionContent.substring(0, 100) + "..." : questionContent) : "无");
            log.info("向量维度: {}", embeddingArray.length);
            log.info("存储时间: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.info("========================");
            return true;

        } catch (Exception e) {
            log.error("=== 学习向量存储失败 ===");
            log.error("用户ID: {}", userId);
            log.error("题目ID: {}", questionId);
            log.error("行为类型: {}", actionType);
            log.error("错误信息: {}", e.getMessage());
            log.error("========================");
            return false;
        }
    }

    @Override
    public List<SimilarQuestionVO> searchSimilarQuestions(String userId, String queryText, int limit) {
        try {
            // 生成查询向量
            float[] queryEmbedding = embeddingService.generateEmbedding(queryText);
            
            // 转换为Float[]数组
            Float[] queryEmbeddingArray = new Float[queryEmbedding.length];
            for (int i = 0; i < queryEmbedding.length; i++) {
                queryEmbeddingArray[i] = queryEmbedding[i];
            }

            // 构建GraphQL查询
            Field[] fields = {
                    Field.builder().name("userId").build(),
                    Field.builder().name("questionId").build(),
                    Field.builder().name("questionContent").build(),
                    Field.builder().name("actionType").build(),
                    Field.builder().name("subject").build(),
                    Field.builder().name("createdAt").build(),
                    Field.builder().name("_additional").fields(
                            Field.builder().name("distance").build()
                    ).build()
            };

            NearVectorArgument nearVector = NearVectorArgument.builder()
                    .vector(queryEmbeddingArray)
                    .build();

            io.weaviate.client.v1.filters.WhereFilter whereFilter = io.weaviate.client.v1.filters.WhereFilter.builder()
                    .path(new String[]{"userId"})
                    .operator(io.weaviate.client.v1.filters.Operator.Equal)
                    .valueText(userId)
                    .build();

            Result<GraphQLResponse> result = weaviateClient.graphQL().get()
                    .withClassName(weaviateClassName)
                    .withFields(fields)
                    .withNearVector(nearVector)
                    .withWhere(whereFilter)
                    .withLimit(limit)
                    .run();

            if (result.hasErrors()) {
                log.error("Weaviate相似度搜索失败: {}", result.getError().getMessages());
                return Collections.emptyList();
            }

            return parseSearchResults(result.getResult());

        } catch (Exception e) {
            log.error("搜索相似题目失败，userId:{} queryText:{}", userId, queryText, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<LearningInsightVO> getUserWeaknesses(String userId) {
        try {
            // 查询用户的错题数据
            List<Map<String, Object>> mistakeData = queryUserDataByActionType(userId, "mistake");
            
            // 分析薄弱点
            return analyzeWeaknesses(mistakeData);

        } catch (Exception e) {
            log.error("获取用户薄弱点失败，userId:{}", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<LearningInsightVO> getUserRecommendations(String userId) {
        try {
            // 获取用户所有学习数据
            List<Map<String, Object>> allData = queryAllUserData(userId);
            
            // 生成学习推荐
            return generateRecommendations(allData);

        } catch (Exception e) {
            log.error("获取用户学习推荐失败，userId:{}", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void analyzeUserLearningPatterns(String userId) {
        try {
            log.info("开始分析用户学习模式，userId:{}", userId);

            // 获取用户所有学习数据
            List<Map<String, Object>> allData = queryAllUserData(userId);

            if (allData.isEmpty()) {
                log.info("用户暂无学习数据，userId:{}", userId);
                return;
            }

            // 分析学习模式并生成洞察
            List<LearningInsightVO> insights = generateLearningInsights(allData);

            // 这里可以将洞察结果存储到其他地方，比如MySQL或Redis
            log.info("用户学习模式分析完成，userId:{} 生成洞察数量:{}", userId, insights.size());

        } catch (Exception e) {
            log.error("分析用户学习模式失败，userId:{}", userId, e);
        }
    }

    /**
     * 查询用户特定行为类型的数据
     */
    private List<Map<String, Object>> queryUserDataByActionType(String userId, String actionType) {
        try {
            Field[] fields = {
                    Field.builder().name("userId").build(),
                    Field.builder().name("questionId").build(),
                    Field.builder().name("questionContent").build(),
                    Field.builder().name("actionType").build(),
                    Field.builder().name("subject").build(),
                    Field.builder().name("knowledgePointId").build(),
                    Field.builder().name("createdAt").build()
            };

            io.weaviate.client.v1.filters.WhereFilter whereFilter = io.weaviate.client.v1.filters.WhereFilter.builder()
                    .operator(io.weaviate.client.v1.filters.Operator.And)
                    .operands(new io.weaviate.client.v1.filters.WhereFilter[]{
                            io.weaviate.client.v1.filters.WhereFilter.builder()
                                    .path(new String[]{"userId"})
                                    .operator(io.weaviate.client.v1.filters.Operator.Equal)
                                    .valueText(userId)
                                    .build(),
                            io.weaviate.client.v1.filters.WhereFilter.builder()
                                    .path(new String[]{"actionType"})
                                    .operator(io.weaviate.client.v1.filters.Operator.Equal)
                                    .valueText(actionType)
                                    .build()
                    })
                    .build();

            Result<GraphQLResponse> result = weaviateClient.graphQL().get()
                    .withClassName(weaviateClassName)
                    .withFields(fields)
                    .withWhere(whereFilter)
                    .withLimit(1000)
                    .run();

            if (result.hasErrors()) {
                log.error("查询用户数据失败: {}", result.getError().getMessages());
                return Collections.emptyList();
            }

            return parseQueryResults(result.getResult());

        } catch (Exception e) {
            log.error("查询用户数据失败，userId:{} actionType:{}", userId, actionType, e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询用户所有数据
     */
    private List<Map<String, Object>> queryAllUserData(String userId) {
        try {
            Field[] fields = {
                    Field.builder().name("userId").build(),
                    Field.builder().name("questionId").build(),
                    Field.builder().name("questionContent").build(),
                    Field.builder().name("actionType").build(),
                    Field.builder().name("subject").build(),
                    Field.builder().name("knowledgePointId").build(),
                    Field.builder().name("createdAt").build()
            };

            io.weaviate.client.v1.filters.WhereFilter whereFilter = io.weaviate.client.v1.filters.WhereFilter.builder()
                    .path(new String[]{"userId"})
                    .operator(io.weaviate.client.v1.filters.Operator.Equal)
                    .valueText(userId)
                    .build();

            Result<GraphQLResponse> result = weaviateClient.graphQL().get()
                    .withClassName(weaviateClassName)
                    .withFields(fields)
                    .withWhere(whereFilter)
                    .withLimit(1000)
                    .run();

            if (result.hasErrors()) {
                log.error("查询用户所有数据失败: {}", result.getError().getMessages());
                return Collections.emptyList();
            }

            return parseQueryResults(result.getResult());

        } catch (Exception e) {
            log.error("查询用户所有数据失败，userId:{}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析搜索结果
     */
    @SuppressWarnings("unchecked")
    private List<SimilarQuestionVO> parseSearchResults(GraphQLResponse response) {
        List<SimilarQuestionVO> results = new ArrayList<>();
        
        try {
            Object dataObj = response.getData();
            if (!(dataObj instanceof Map)) {
                log.warn("GraphQL响应数据格式不正确");
                return results;
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            if (data != null && data.containsKey("Get")) {
                Map<String, Object> get = (Map<String, Object>) data.get("Get");
                if (get.containsKey(weaviateClassName)) {
                    List<Map<String, Object>> objects = (List<Map<String, Object>>) get.get(weaviateClassName);
                    
                    for (Map<String, Object> obj : objects) {
                        SimilarQuestionVO vo = SimilarQuestionVO.builder()
                                .questionId((String) obj.get("questionId"))
                                .questionContent((String) obj.get("questionContent"))
                                .actionType((String) obj.get("actionType"))
                                .subject((String) obj.get("subject"))
                                .createdAt((String) obj.get("createdAt"))
                                .build();
                        
                        // 获取相似度分数
                        if (obj.containsKey("_additional")) {
                            Map<String, Object> additional = (Map<String, Object>) obj.get("_additional");
                            if (additional.containsKey("distance")) {
                                Double distance = (Double) additional.get("distance");
                                // 将距离转换为相似度 (1 - distance)
                                vo.setSimilarity(1.0 - distance);
                            }
                        }
                        
                        results.add(vo);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析搜索结果失败", e);
        }
        
        return results;
    }

    /**
     * 解析查询结果
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseQueryResults(GraphQLResponse response) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            Object dataObj = response.getData();
            if (!(dataObj instanceof Map)) {
                log.warn("GraphQL响应数据格式不正确");
                return results;
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            if (data != null && data.containsKey("Get")) {
                Map<String, Object> get = (Map<String, Object>) data.get("Get");
                if (get.containsKey(weaviateClassName)) {
                    List<Map<String, Object>> objects = (List<Map<String, Object>>) get.get(weaviateClassName);
                    results.addAll(objects);
                }
            }
        } catch (Exception e) {
            log.error("解析查询结果失败", e);
        }
        
        return results;
    }

    // 以下方法与原VectorRepository中的实现类似，但使用Map<String, Object>而不是LearningVector

    private String buildVectorText(String actionType, String questionContent, String subject) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("用户").append(getActionDescription(actionType));
        if (subject != null && !subject.trim().isEmpty()) {
            textBuilder.append("了一道").append(subject).append("题目：");
        } else {
            textBuilder.append("了一道题目：");
        }
        if (questionContent != null && !questionContent.trim().isEmpty()) {
            String content = questionContent.trim();
            if (content.length() > 500) {
                content = content.substring(0, 500) + "...";
            }
            textBuilder.append(content);
        }
        return textBuilder.toString();
    }

    private String getActionDescription(String actionType) {
        switch (actionType) {
            case "upload": return "上传";
            case "review": return "复习";
            case "qa": return "问答";
            case "mistake": return "做错";
            default: return "学习";
        }
    }

    private Map<String, Object> buildMetadata(String actionType, String subject, Integer knowledgePointId) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("actionType", actionType);
        if (subject != null) metadata.put("subject", subject);
        if (knowledgePointId != null) metadata.put("knowledgePointId", knowledgePointId);
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("embeddingModel", "dashscope-text-embedding-v1");
        return metadata;
    }

    private List<LearningInsightVO> analyzeWeaknesses(List<Map<String, Object>> vectors) {
        List<LearningInsightVO> insights = new ArrayList<>();
        if (vectors.isEmpty()) return insights;

        // 按科目分组分析
        Map<String, List<Map<String, Object>>> subjectGroups = vectors.stream()
                .filter(v -> v.get("subject") != null && !v.get("subject").toString().trim().isEmpty())
                .collect(Collectors.groupingBy(v -> v.get("subject").toString()));

        for (Map.Entry<String, List<Map<String, Object>>> entry : subjectGroups.entrySet()) {
            String subject = entry.getKey();
            List<Map<String, Object>> subjectVectors = entry.getValue();

            if (subjectVectors.size() >= 3) {
                insights.add(LearningInsightVO.builder()
                        .type("weakness")
                        .title(subject + "学科薄弱")
                        .description(String.format("在%s学科中错误%d道题目，建议加强练习", subject, subjectVectors.size()))
                        .confidenceScore(Math.min(0.9, 0.5 + subjectVectors.size() * 0.1))
                        .relatedQuestions(subjectVectors.stream()
                                .map(v -> v.get("questionId").toString())
                                .collect(Collectors.toList()))
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .build());
            }
        }

        return insights;
    }

    private List<LearningInsightVO> generateRecommendations(List<Map<String, Object>> vectors) {
        List<LearningInsightVO> recommendations = new ArrayList<>();
        
        if (vectors.isEmpty()) {
            recommendations.add(LearningInsightVO.builder()
                    .type("recommendation")
                    .title("开始学习之旅")
                    .description("还没有学习记录，建议开始上传题目进行学习")
                    .confidenceScore(1.0)
                    .createdAt(LocalDateTime.now())
                    .isActive(true)
                    .build());
            return recommendations;
        }

        // 分析学习频率等逻辑...
        return recommendations;
    }

    private List<LearningInsightVO> generateLearningInsights(List<Map<String, Object>> vectors) {
        List<LearningInsightVO> insights = new ArrayList<>();
        // 生成学习洞察的逻辑...
        return insights;
    }
}