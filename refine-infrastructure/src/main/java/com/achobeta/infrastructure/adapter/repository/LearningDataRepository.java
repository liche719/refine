package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.rag.adapter.port.ILearningDataRepository;
import com.achobeta.domain.rag.model.entity.LearningVectorEntity;
import com.achobeta.domain.rag.model.valobj.LearningStatisticsVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.fields.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auth : Malog
 * @Desc : 学习数据仓储实现类
 * @Time : 2025/11/25
 */
@Slf4j
@Repository
public class LearningDataRepository implements ILearningDataRepository {

    @Autowired
    private WeaviateClient weaviateClient;

    @Autowired
    private String weaviateClassName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<LearningVectorEntity> getUserRecentLearningData(String userId, int days) {
        try {
            log.info("获取用户最近{}天的学习向量数据，userId:{}, days:{}", days, userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法查询学习数据");
                return Collections.emptyList();
            }
            
            if (days <= 0) {
                log.warn("查询天数无效，days:{}", days);
                return Collections.emptyList();
            }
            
            // 使用Weaviate查询用户最近N天的学习数据
            List<Map<String, Object>> rawData = queryUserDataWithTimeFilter(userId, days);
            
            // 转换为LearningVectorEntity
            return rawData.stream()
                    .map(this::convertToLearningVectorEntity)
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // 按时间倒序
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取用户学习向量数据失败，userId:{}, days:{}", userId, days, e);
            return Collections.emptyList();
        }
    }

    @Override
    public LearningStatisticsVO getUserLearningStatistics(String userId, int days) {
        try {
            log.info("获取用户最近{}天的学习统计数据，userId:{}, days:{}", days, userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法查询学习统计数据");
                return null;
            }
            
            if (days <= 0) {
                log.warn("查询天数无效，days:{}", days);
                return null;
            }
            
            // 使用Weaviate查询用户学习统计数据
            List<Map<String, Object>> rawData = queryUserDataWithTimeFilter(userId, days);
            
            if (rawData.isEmpty()) {
                return LearningStatisticsVO.builder()
                        .totalActivities(0)
                        .subjectsCount(0)
                        .activeDays(0)
                        .actionTypesCount(0)
                        .build();
            }
            
            // 计算统计数据
            int totalActivities = rawData.size();
            
            Set<String> subjects = rawData.stream()
                    .map(data -> (String) data.get("subject"))
                    .filter(Objects::nonNull)
                    .filter(s -> !s.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            Set<String> actionTypes = rawData.stream()
                    .map(data -> (String) data.get("actionType"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            Set<String> activeDates = rawData.stream()
                    .map(data -> (String) data.get("createdAt"))
                    .filter(Objects::nonNull)
                    .map(dateStr -> {
                        try {
                            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            return dateTime.toLocalDate().toString();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            LocalDateTime firstActivity = rawData.stream()
                    .map(data -> (String) data.get("createdAt"))
                    .filter(Objects::nonNull)
                    .map(dateStr -> {
                        try {
                            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            
            LocalDateTime lastActivity = rawData.stream()
                    .map(data -> (String) data.get("createdAt"))
                    .filter(Objects::nonNull)
                    .map(dateStr -> {
                        try {
                            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            
            return LearningStatisticsVO.builder()
                    .totalActivities(totalActivities)
                    .subjectsCount(subjects.size())
                    .activeDays(activeDates.size())
                    .actionTypesCount(actionTypes.size())
                    .firstActivity(firstActivity)
                    .lastActivity(lastActivity)
                    .build();
            
        } catch (Exception e) {
            log.error("获取用户学习统计数据失败，userId:{}, days:{}", userId, days, e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getUserLearningDataBySubject(String userId, int days) {
        try {
            log.info("获取用户最近{}天按科目分组的学习数据，userId:{}, days:{}", days, userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法查询按科目分组的学习数据");
                return Collections.emptyList();
            }
            
            if (days <= 0) {
                log.warn("查询天数无效，days:{}", days);
                return Collections.emptyList();
            }
            
            // 使用Weaviate查询用户学习数据
            List<Map<String, Object>> rawData = queryUserDataWithTimeFilter(userId, days);
            
            if (rawData.isEmpty()) {
                return Collections.emptyList();
            }
            
            // 按科目分组统计
            Map<String, List<Map<String, Object>>> subjectGroups = rawData.stream()
                    .filter(data -> data.get("subject") != null && !data.get("subject").toString().trim().isEmpty())
                    .collect(Collectors.groupingBy(data -> data.get("subject").toString()));
            
            // 转换为结果格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : subjectGroups.entrySet()) {
                String subject = entry.getKey();
                List<Map<String, Object>> subjectData = entry.getValue();
                
                Set<String> actionTypes = subjectData.stream()
                        .map(data -> (String) data.get("actionType"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                
                Map<String, Object> subjectStat = new HashMap<>();
                subjectStat.put("subject", subject);
                subjectStat.put("activity_count", subjectData.size());
                subjectStat.put("action_list", String.join(",", actionTypes));
                result.add(subjectStat);
            }
            
            // 按活动数量降序排序
            result.sort((a, b) -> {
                Integer countA = (Integer) a.get("activity_count");
                Integer countB = (Integer) b.get("activity_count");
                return countB.compareTo(countA);
            });
            
            return result;
            
        } catch (Exception e) {
            log.error("获取用户按科目分组学习数据失败，userId:{}, days:{}", userId, days, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getUserLearningDataByActionType(String userId, int days) {
        try {
            log.info("获取用户最近{}天按行为类型分组的学习数据，userId:{}, days:{}", days, userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法查询按行为类型分组的学习数据");
                return Collections.emptyList();
            }
            
            if (days <= 0) {
                log.warn("查询天数无效，days:{}", days);
                return Collections.emptyList();
            }
            
            // 使用Weaviate查询用户学习数据
            List<Map<String, Object>> rawData = queryUserDataWithTimeFilter(userId, days);
            
            if (rawData.isEmpty()) {
                return Collections.emptyList();
            }
            
            // 按行为类型分组统计
            Map<String, List<Map<String, Object>>> actionGroups = rawData.stream()
                    .filter(data -> data.get("actionType") != null)
                    .collect(Collectors.groupingBy(data -> data.get("actionType").toString()));
            
            // 转换为结果格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : actionGroups.entrySet()) {
                String actionType = entry.getKey();
                List<Map<String, Object>> actionData = entry.getValue();
                
                Set<String> subjects = actionData.stream()
                        .map(data -> (String) data.get("subject"))
                        .filter(Objects::nonNull)
                        .filter(s -> !s.trim().isEmpty())
                        .collect(Collectors.toSet());
                
                Map<String, Object> actionStat = new HashMap<>();
                actionStat.put("action_type", actionType);
                actionStat.put("activity_count", actionData.size());
                actionStat.put("subjects_count", subjects.size());
                result.add(actionStat);
            }
            
            // 按活动数量降序排序
            result.sort((a, b) -> {
                Integer countA = (Integer) a.get("activity_count");
                Integer countB = (Integer) b.get("activity_count");
                return countB.compareTo(countA);
            });
            
            return result;
            
        } catch (Exception e) {
            log.error("获取用户按行为类型分组学习数据失败，userId:{}, days:{}", userId, days, e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询用户指定时间范围内的学习数据
     */
    private List<Map<String, Object>> queryUserDataWithTimeFilter(String userId, int days) {
        try {
            // 计算时间范围
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(days);
            
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
                                    .path(new String[]{"createdAt"})
                                    .operator(io.weaviate.client.v1.filters.Operator.GreaterThanEqual)
                                    .valueText(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
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
                log.error("查询用户学习数据失败: {}", result.getError().getMessages());
                return Collections.emptyList();
            }

            return parseQueryResults(result.getResult());

        } catch (Exception e) {
            log.error("查询用户学习数据失败，userId:{} days:{}", userId, days, e);
            return Collections.emptyList();
        }
    }

    /**
     * 解析Weaviate查询结果
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseQueryResults(GraphQLResponse response) {
        try {
            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }

            // 安全地转换getData()的结果
            Object dataObj = response.getData();
            if (!(dataObj instanceof Map)) {
                log.warn("Weaviate查询结果数据格式不正确，期望Map类型，实际类型: {}", dataObj.getClass().getSimpleName());
                return Collections.emptyList();
            }

            Map<String, Object> data = (Map<String, Object>) dataObj;
            Map<String, Object> get = (Map<String, Object>) data.get("Get");
            if (get == null) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> objects = (List<Map<String, Object>>) get.get(weaviateClassName);
            if (objects == null) {
                return Collections.emptyList();
            }

            return objects;

        } catch (Exception e) {
            log.error("解析Weaviate查询结果失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 将Weaviate查询结果转换为LearningVectorEntity
     */
    private LearningVectorEntity convertToLearningVectorEntity(Map<String, Object> data) {
        try {
            if (data == null) {
                return null;
            }

            LocalDateTime createdAt = null;
            String createdAtStr = (String) data.get("createdAt");
            if (createdAtStr != null) {
                try {
                    createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e) {
                    log.warn("解析创建时间失败: {}", createdAtStr);
                }
            }

            Integer knowledgePointId = null;
            Object kpId = data.get("knowledgePointId");
            if (kpId != null) {
                if (kpId instanceof Number) {
                    knowledgePointId = ((Number) kpId).intValue();
                } else {
                    try {
                        knowledgePointId = Integer.parseInt(kpId.toString());
                    } catch (NumberFormatException e) {
                        log.warn("解析知识点ID失败: {}", kpId);
                    }
                }
            }

            return LearningVectorEntity.builder()
                    .userId((String) data.get("userId"))
                    .questionId((String) data.get("questionId"))
                    .questionContent((String) data.get("questionContent"))
                    .actionType((String) data.get("actionType"))
                    .subject((String) data.get("subject"))
                    .knowledgePointId(knowledgePointId)
                    .createdAt(createdAt)
                    .build();

        } catch (Exception e) {
            log.error("转换LearningVectorEntity失败", e);
            return null;
        }
    }

    /**
     * 将数据库统计结果转换为统计VO
     */
    private LearningStatisticsVO convertToStatisticsVO(Map<String, Object> statisticsMap) {
        if (statisticsMap == null || statisticsMap.isEmpty()) {
            return LearningStatisticsVO.builder()
                    .totalActivities(0)
                    .subjectsCount(0)
                    .activeDays(0)
                    .actionTypesCount(0)
                    .build();
        }
        
        return LearningStatisticsVO.builder()
                .totalActivities(getIntValue(statisticsMap, "total_activities"))
                .actionTypesCount(getIntValue(statisticsMap, "action_types_count"))
                .subjectsCount(getIntValue(statisticsMap, "subjects_count"))
                .activeDays(getIntValue(statisticsMap, "active_days"))
                .firstActivity(getDateTimeValue(statisticsMap, "first_activity"))
                .lastActivity(getDateTimeValue(statisticsMap, "last_activity"))
                .build();
    }

    /**
     * 安全获取整数值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("无法解析整数值: {} = {}", key, value);
            return 0;
        }
    }

    /**
     * 安全获取日期时间值
     */
    private LocalDateTime getDateTimeValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        
        // 如果是其他类型，可以根据需要添加转换逻辑
        return null;
    }
}