package com.achobeta.domain.rag.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Auth : Malog
 * @Desc : 学习向量实体类
 * @Time : 2025/11/25 21:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningVectorEntity {

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
}
