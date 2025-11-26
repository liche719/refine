package com.achobeta.infrastructure.dao.po;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Auth : Malog
 * @Desc : 学习向量数据库实体
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningVector {
    
    private Long id;
    private String userId;
    private String questionId;
    private String actionType;
    private String questionContent;
    private String subject;
    private Integer knowledgePointId;
    private float[] embedding;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 相似度分数（查询时使用）
     */
    private Double similarity;
}
